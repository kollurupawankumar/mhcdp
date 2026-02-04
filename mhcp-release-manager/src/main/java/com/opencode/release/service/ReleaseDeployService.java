package com.opencode.release.service;

import com.opencode.release.compiler.MetadataCompiler;
import com.opencode.release.config.ReleaseManagerProps;
import com.opencode.release.loader.YamlReader;
import com.opencode.release.model.*;
import com.opencode.release.repo.Layer2MetadataRepo;
import com.opencode.release.repo.MetadataRepo;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class ReleaseDeployService {

    private final YamlReader yaml;
    private final MetadataRepo repo;
    private final ValidationService validator;
    private final ReleaseManagerProps props;
    private final Layer2MetadataRepo layer2Repo;
    private final MetadataCompiler compiler;

    // ✅ cache for resolving refs during compilation
    private ConnectionBundle connectionBundle;
    private EnrichmentBundle enrichmentBundle;

    public ReleaseDeployService(
            YamlReader yaml,
            MetadataRepo repo,
            ValidationService validator,
            ReleaseManagerProps props,
            Layer2MetadataRepo layer2Repo,
            MetadataCompiler metadataCompiler
    ) {
        this.yaml = yaml;
        this.repo = repo;
        this.validator = validator;
        this.props = props;
        this.layer2Repo = layer2Repo;
        this.compiler = metadataCompiler;
    }

    @Transactional
    public void deploy(String releaseYamlPath, String gitCommit) {

        MetadataReleaseFile release =
                yaml.read(new FileSystemResource(releaseYamlPath), MetadataReleaseFile.class);

        String releaseId = release.metadata().name();
        String env = release.spec().env();

        repo.createRelease(releaseId, env, gitCommit);

        // Load bundles referenced in release
        for (var b : release.spec().bundles()) {
            if ("DomainBundle".equalsIgnoreCase(b.kind())) {
                deployDomainBundle(releaseId, b.file());
            } else if ("EnrichmentBundle".equalsIgnoreCase(b.kind())) {
                deployEnrichmentBundle(releaseId, b.file());
            } else if ("ConnectionBundle".equalsIgnoreCase(b.kind())) {
                deployConnectionBundle(releaseId, b.file());
            } else if ("PipelineBundle".equalsIgnoreCase(b.kind())) {
                deployPipelineBundle(releaseId, b.file());
            } else {
                throw new IllegalArgumentException("Unsupported bundle kind: " + b.kind());
            }
        }

        // Activate only requested objects
        activateFromRelease(releaseId, release);

        repo.markReleaseActive(releaseId);
    }

    private void deployDomainBundle(String releaseId, String bundlePath) {
        DomainBundle bundle = yaml.read(new FileSystemResource(bundlePath), DomainBundle.class);

        String domain = bundle.metadata().domain();
        int version = bundle.metadata().version();

        // ✅ Make sure domain exists in layer-2
        layer2Repo.upsertDomain(domain);

        // templates
        if (bundle.spec().pipelineTemplates() != null) {
            for (var t : bundle.spec().pipelineTemplates()) {
                repo.upsertObject(ObjectType.PIPELINE_TEMPLATE.name(), t.name(), version, domain, t, "DRAFT", releaseId);
                repo.addReleaseItem(releaseId, ObjectType.PIPELINE_TEMPLATE.name(), t.name(), version);
            }
        }

        // transformations
        if (bundle.spec().transformations() != null) {
            for (var tr : bundle.spec().transformations()) {
                repo.upsertObject(ObjectType.TRANSFORMATION.name(), tr.name(), version, domain, tr, "DRAFT", releaseId);
                repo.addReleaseItem(releaseId, ObjectType.TRANSFORMATION.name(), tr.name(), version);
            }
        }

        // ✅ Build in-memory indexes for compiler
        var trIndex = compiler.indexTransformations(bundle);

        Map<String, EnrichmentBundle.EnrichmentDef> enrichIndex =
                (enrichmentBundle == null) ? Map.of() : compiler.indexEnrichments(enrichmentBundle);

        // datasets
        if (bundle.spec().datasets() != null) {
            for (var ds : bundle.spec().datasets()) {

                // layer-1 validation
                validator.validateDatasetRefs(domain, ds);

                // store dataset into object store
                repo.upsertObject(ObjectType.DATASET.name(), ds.name(), version, domain, ds, "DRAFT", releaseId);
                repo.addReleaseItem(releaseId, ObjectType.DATASET.name(), ds.name(), version);

                // ✅ Compile and store runtime metadata (layer-2)
                var compiled = compiler.compile(
                        domain,
                        version,
                        ds,
                        connectionBundle,
                        trIndex,
                        enrichIndex
                );

                layer2Repo.upsertEntity(domain, compiled.entityId(), compiled.entityName());
                layer2Repo.upsertSourceMetadata(compiled, version);
                layer2Repo.upsertTransformationMetadata(compiled, version);
                layer2Repo.upsertEnrichmentMetadata(compiled, version);

                // ✅ Keep only current version active for that entity
                layer2Repo.deactivateOldVersions(compiled.entityId(), version);
            }
        }

        // If you still allow connections inside DomainBundle (not recommended)
        if (bundle.spec().connections() != null) {
            for (var c : bundle.spec().connections()) {
                repo.upsertObject(
                        ObjectType.CONNECTION_PROFILE.name(),
                        c.name(),
                        version,
                        domain,
                        c,
                        "DRAFT",
                        releaseId
                );
                repo.addReleaseItem(releaseId, ObjectType.CONNECTION_PROFILE.name(), c.name(), version);
            }
        }
    }

    private void deployEnrichmentBundle(String releaseId, String bundlePath) {
        EnrichmentBundle bundle = yaml.read(new FileSystemResource(bundlePath), EnrichmentBundle.class);

        this.enrichmentBundle = bundle; // ✅ cache for compiler resolution

        String domain = bundle.metadata().domain();
        int version = bundle.metadata().version();

        for (var e : bundle.spec().enrichments()) {
            validator.validateEnrichment(e);
            repo.upsertObject(ObjectType.ENRICHMENT.name(), e.name(), version, domain, e, "DRAFT", releaseId);
            repo.addReleaseItem(releaseId, ObjectType.ENRICHMENT.name(), e.name(), version);
        }
    }

    private void deployConnectionBundle(String releaseId, String bundlePath) {
        var bundle = yaml.read(new FileSystemResource(bundlePath), ConnectionBundle.class);

        this.connectionBundle = bundle; // ✅ cache for compiler resolution

        String domain = bundle.metadata().domain();
        int version = bundle.metadata().version();

        // store defaults
        if (bundle.spec().defaults() != null) {
            repo.upsertObject("DOMAIN_DEFAULTS", domain, version, domain, bundle.spec().defaults(), "DRAFT", releaseId);
            repo.addReleaseItem(releaseId, "DOMAIN_DEFAULTS", domain, version);
        }

        // store each connection profile
        if (bundle.spec().connections() != null) {
            for (var c : bundle.spec().connections()) {
                repo.upsertObject(
                        ObjectType.CONNECTION_PROFILE.name(),
                        c.name(),
                        version,
                        domain,
                        c,
                        "DRAFT",
                        releaseId
                );
                repo.addReleaseItem(releaseId, ObjectType.CONNECTION_PROFILE.name(), c.name(), version);
            }
        }
    }

    private void deployPipelineBundle(String releaseId, String bundlePath) {
        PipelineBundle bundle = yaml.read(new FileSystemResource(bundlePath), PipelineBundle.class);

        String domain = bundle.metadata().domain();
        int version = bundle.metadata().version();

        if (bundle.spec().pipelineTemplates() != null) {
            for (var p : bundle.spec().pipelineTemplates()) {
                repo.upsertObject(
                        ObjectType.PIPELINE_TEMPLATE.name(),
                        p.name(),
                        version,
                        domain,
                        p,
                        "DRAFT",
                        releaseId
                );
                repo.addReleaseItem(releaseId, ObjectType.PIPELINE_TEMPLATE.name(), p.name(), version);
            }
        }
    }

    private void activateFromRelease(String releaseId, MetadataReleaseFile release) {
        // Activate datasets
        if (release.spec().activate() != null && release.spec().activate().datasets() != null) {
            for (String dsName : release.spec().activate().datasets()) {
                repo.deactivateAllActiveForName(ObjectType.DATASET.name(), dsName);

                // NOTE:
                // In a full impl you would do:
                // repo.activateLatestDraftFromRelease(ObjectType.DATASET.name(), dsName, releaseId);
            }
        }
    }
}

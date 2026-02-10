package com.opencode.release.service;

import com.opencode.release.compiler.MetadataCompiler;
import com.opencode.release.loader.YamlReader;
import com.opencode.release.model.*;
import com.opencode.release.repo.MetadataRepo;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReleaseDeployService {

    private final YamlReader yaml;
    private final MetadataRepo repo;
    private final ValidationService validator;
    private final MetadataCompiler compiler;

    public ReleaseDeployService(
            YamlReader yaml,
            MetadataRepo repo,
            ValidationService validator,
            MetadataCompiler compiler) {

        this.yaml = yaml;
        this.repo = repo;
        this.validator = validator;
        this.compiler = compiler;
    }

    // ---------------------------------------------------------
    // ENTRY METHOD
    // ---------------------------------------------------------
    @Transactional
    public void deploy(
            String releaseYamlPath,
            String gitCommit) {

        MetadataReleaseFile release =
                yaml.read(
                        new FileSystemResource(releaseYamlPath),
                        MetadataReleaseFile.class
                );

        String releaseId =
                release.metadata().name();

        String env =
                release.spec().env();

        // Create release record
        repo.createRelease(
                releaseId,
                env,
                gitCommit
        );

        // Deploy bundles
        for (var b : release.spec().bundles()) {

            if ("IngestionBundle".equalsIgnoreCase(b.kind())) {

                deployIngestionBundle(
                        releaseId,
                        b.file()
                );

            } else {

                throw new IllegalArgumentException(
                        "Unsupported bundle kind: "
                                + b.kind()
                );
            }
        }

        repo.markReleaseActive(releaseId);
    }

    // ---------------------------------------------------------
    // INGESTION BUNDLE DEPLOYMENT
    // ---------------------------------------------------------
    private void deployIngestionBundle(
            String releaseId,
            String bundlePath) {

        // 1️⃣ Read YAML
        IngestionPipelineYaml yamlObj =
                yaml.read(
                        new FileSystemResource(bundlePath),
                        IngestionPipelineYaml.class
                );

        String pipelineName =
                yamlObj.ingestion().pipeline_name();

        String domain =
                yamlObj.ingestion().domain();

        // 2️⃣ Validate
        validator.validateIngestionYaml(yamlObj);

        // 3️⃣ Version resolution
        int version =
                repo.getNextVersion(
                        ObjectType.INGESTION_PIPELINE.name(),
                        pipelineName
                );

        // 4️⃣ Layer-1 storage
        repo.upsertObject(
                ObjectType.INGESTION_PIPELINE.name(),
                pipelineName,
                version,
                domain,
                yamlObj,
                "DRAFT",
                releaseId
        );

        repo.addReleaseItem(
                releaseId,
                ObjectType.INGESTION_PIPELINE.name(),
                pipelineName,
                version
        );

        // 5️⃣ Compile → Layer-2
        compiler.compile(
                yamlObj,
                version
        );

        System.out.println(
                "✔ Deployed ingestion pipeline → "
                        + pipelineName
                        + " v"
                        + version
        );
    }
}

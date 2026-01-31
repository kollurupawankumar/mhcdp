package com.mdhp.metadata.controller;

import com.mdhp.metadata.model.EnrichmentMetadataEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mdhp.metadata.repo.EnrichmentMetadataRepository;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/enrichment-metadata")
public class EnrichmentMetadataController {

  @Autowired
  private EnrichmentMetadataRepository repository;

  @GetMapping
  public List<EnrichmentMetadataEntity> list() {
    return repository.findAll();
  }

  @PostMapping
  public EnrichmentMetadataEntity create(@RequestBody EnrichmentMetadataEntity em) {
    return repository.save(em);
  }

  @GetMapping("/{id}")
  public EnrichmentMetadataEntity get(@PathVariable Long id) {
    Optional<EnrichmentMetadataEntity> o = repository.findById(id);
    return o.orElse(null);
  }

  @PutMapping("/{id}")
  public EnrichmentMetadataEntity update(@PathVariable Long id, @RequestBody EnrichmentMetadataEntity em) {
    em.setId(id);
    return repository.save(em);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    repository.deleteById(id);
  }
}

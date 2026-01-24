package com.mdhp.metadata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mdhp.metadata.model.EnrichmentMetadata;
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
  public List<EnrichmentMetadata> list() {
    return repository.findAll();
  }

  @PostMapping
  public EnrichmentMetadata create(@RequestBody EnrichmentMetadata em) {
    return repository.save(em);
  }

  @GetMapping("/{id}")
  public EnrichmentMetadata get(@PathVariable Long id) {
    Optional<EnrichmentMetadata> o = repository.findById(id);
    return o.orElse(null);
  }

  @PutMapping("/{id}")
  public EnrichmentMetadata update(@PathVariable Long id, @RequestBody EnrichmentMetadata em) {
    em.setId(id);
    return repository.save(em);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    repository.deleteById(id);
  }
}

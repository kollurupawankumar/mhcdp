package com.mdhp.metadata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mdhp.metadata.model.SourceMetadata;
import com.mdhp.metadata.repo.SourceMetadataRepository;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/source-metadata")
public class SourceMetadataController {

  @Autowired
  private SourceMetadataRepository repository;

  @GetMapping
  public List<SourceMetadata> list() {
    return repository.findAll();
  }

  @PostMapping
  public SourceMetadata create(@RequestBody SourceMetadata sm) {
    return repository.save(sm);
  }

  @GetMapping("/{id}")
  public SourceMetadata get(@PathVariable Long id) {
    Optional<SourceMetadata> o = repository.findById(id);
    return o.orElse(null);
  }

  @PutMapping("/{id}")
  public SourceMetadata update(@PathVariable Long id, @RequestBody SourceMetadata sm) {
    sm.setId(id);
    return repository.save(sm);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    repository.deleteById(id);
  }
}

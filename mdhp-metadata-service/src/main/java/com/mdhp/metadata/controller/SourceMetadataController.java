package com.mdhp.metadata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mdhp.metadata.model.*;
import com.mdhp.metadata.repo.SourceMetadataRepository;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/source-metadata")
public class SourceMetadataController {

  @Autowired
  private SourceMetadataRepository repository;

  @GetMapping
  public List<SourceMetadataEntity> list() {
    return repository.findAll();
  }

  @PostMapping
  public SourceMetadataEntity create(@RequestBody SourceMetadataEntity sm) {
    return repository.save(sm);
  }

  @GetMapping("/{id}")
  public SourceMetadataEntity get(@PathVariable Long id) {
    Optional<SourceMetadataEntity> o = repository.findById(id);
    return o.orElse(null);
  }

  @PutMapping("/{id}")
  public SourceMetadataEntity update(@PathVariable Long id, @RequestBody SourceMetadataEntity sm) {
    sm.setId(id);
    return repository.save(sm);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    repository.deleteById(id);
  }
}

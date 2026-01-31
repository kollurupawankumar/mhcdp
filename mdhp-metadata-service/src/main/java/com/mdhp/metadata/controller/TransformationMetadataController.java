package com.mdhp.metadata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mdhp.metadata.model.*;
import com.mdhp.metadata.repo.TransformationMetadataRepository;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/transformation-metadata")
public class TransformationMetadataController {

  @Autowired
  private TransformationMetadataRepository repository;

  @GetMapping
  public List<TransformationMetadataEntity> list() {
    return repository.findAll();
  }

  @PostMapping
  public TransformationMetadataEntity create(@RequestBody TransformationMetadataEntity tm) {
    return repository.save(tm);
  }

  @GetMapping("/{id}")
  public TransformationMetadataEntity get(@PathVariable Long id) {
    Optional<TransformationMetadataEntity> o = repository.findById(id);
    return o.orElse(null);
  }

  @PutMapping("/{id}")
  public TransformationMetadataEntity update(@PathVariable Long id, @RequestBody TransformationMetadataEntity tm) {
    tm.setId(id);
    return repository.save(tm);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    repository.deleteById(id);
  }
}

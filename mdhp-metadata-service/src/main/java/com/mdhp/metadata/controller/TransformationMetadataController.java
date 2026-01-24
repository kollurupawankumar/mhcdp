package com.mdhp.metadata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mdhp.metadata.model.TransformationMetadata;
import com.mdhp.metadata.repo.TransformationMetadataRepository;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/transformation-metadata")
public class TransformationMetadataController {

  @Autowired
  private TransformationMetadataRepository repository;

  @GetMapping
  public List<TransformationMetadata> list() {
    return repository.findAll();
  }

  @PostMapping
  public TransformationMetadata create(@RequestBody TransformationMetadata tm) {
    return repository.save(tm);
  }

  @GetMapping("/{id}")
  public TransformationMetadata get(@PathVariable Long id) {
    Optional<TransformationMetadata> o = repository.findById(id);
    return o.orElse(null);
  }

  @PutMapping("/{id}")
  public TransformationMetadata update(@PathVariable Long id, @RequestBody TransformationMetadata tm) {
    tm.setId(id);
    return repository.save(tm);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    repository.deleteById(id);
  }
}

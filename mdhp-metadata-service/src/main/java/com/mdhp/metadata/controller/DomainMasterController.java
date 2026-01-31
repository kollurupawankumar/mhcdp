package com.mdhp.metadata.controller;

import com.mdhp.metadata.model.DomainMasterEntity;
import com.mdhp.metadata.repo.DomainMasterRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/domains")
public class DomainMasterController {

    @Autowired
    private DomainMasterRepository domainRepo;

    // ✅ CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DomainMasterEntity create(@Valid @RequestBody DomainMasterEntity req) {
        // duplicate validation
        if (domainRepo.findByDomainCode(req.getDomainCode()).isPresent()) {
            throw new IllegalArgumentException("Domain already exists with domainCode=" + req.getDomainCode());
        }
        if (req.getActiveFlag() == null) req.setActiveFlag(true);
        return domainRepo.save(req);
    }

    // ✅ READ ALL
    @GetMapping
    public List<DomainMasterEntity> getAll(
            @RequestParam(required = false) Boolean activeOnly
    ) {
        if (activeOnly != null && activeOnly) {
            List<DomainMasterEntity> list = new ArrayList<>();
            for (DomainMasterEntity d : domainRepo.findAll()) {
                if (Boolean.TRUE.equals(d.getActiveFlag())) {
                    list.add(d);
                }
            }
            return list;
        }
        return domainRepo.findAll();
    }

    // ✅ READ ONE
    @GetMapping("/{domainCode}")
    public DomainMasterEntity get(@PathVariable String domainCode) {
        return domainRepo.findByDomainCode(domainCode)
                .orElseThrow(() -> new RuntimeException("Domain not found: " + domainCode));
    }

    // ✅ UPDATE
    @PutMapping("/{domainCode}")
    public DomainMasterEntity update(
            @PathVariable String domainCode,
            @RequestBody DomainMasterEntity req
    ) {
        DomainMasterEntity existing = domainRepo.findByDomainCode(domainCode)
                .orElseThrow(() -> new RuntimeException("Domain not found: " + domainCode));

        // update only allowed fields
        if (req.getDomainName() != null) existing.setDomainName(req.getDomainName());
        if (req.getRunFrequency() != null) existing.setRunFrequency(req.getRunFrequency());
        if (req.getActiveFlag() != null) existing.setActiveFlag(req.getActiveFlag());

        return domainRepo.save(existing);
    }

    // ✅ DELETE
    @DeleteMapping("/{domainCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String domainCode) {
        DomainMasterEntity existing = domainRepo.findByDomainCode(domainCode)
                .orElseThrow(() -> new RuntimeException("Domain not found: " + domainCode));
        domainRepo.delete(existing);
    }

    // ✅ ACTIVATE / DEACTIVATE
    @PatchMapping("/{domainCode}/active")
    public DomainMasterEntity setActive(
            @PathVariable String domainCode,
            @RequestParam boolean value
    ) {
        DomainMasterEntity existing = domainRepo.findByDomainCode(domainCode)
                .orElseThrow(() -> new RuntimeException("Domain not found: " + domainCode));

        existing.setActiveFlag(value);
        return domainRepo.save(existing);
    }
}

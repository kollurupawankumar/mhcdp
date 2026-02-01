package com.mdhp.orchestrator.controller;

import com.mdhp.common.dto.PipelineRunRequest;
import com.mdhp.common.dto.PipelineRunResponse;
import com.mdhp.common.dto.PipelineRunStatusResponse;


import com.mdhp.orchestrator.entity.PipelineRunEntity;
import com.mdhp.orchestrator.repository.PipelineRunRepository;
import com.mdhp.orchestrator.service.PipelineRunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pipeline_runs")
public class PipelineRunController {

    @Autowired
    private PipelineRunService service;

    @Autowired
    private PipelineRunRepository runRepo;

    /**
     * Create a new pipeline run
     * POST /pipeline_runs
     */
    @PostMapping
    public ResponseEntity<PipelineRunResponse> trigger(@RequestBody PipelineRunRequest request) throws Exception {
        String runId = service.createPipelineRun(request);
        return ResponseEntity.ok(new PipelineRunResponse(runId, "RUNNING"));
    }

    /**
     * Get pipeline run status
     * GET /pipeline_runs/{runId}
     */
    @GetMapping("/{runId}")
    public ResponseEntity<PipelineRunStatusResponse> getStatus(@PathVariable String runId) {
        return runRepo.findByRunId(runId)
                .map(this::toStatusResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private PipelineRunStatusResponse toStatusResponse(PipelineRunEntity run) {
        return new PipelineRunStatusResponse(
                run.getRunId(),
                run.getDomainCode(),
                run.getEntityId(),
                run.getStatus(),
                run.getStartedAt(),
                run.getCompletedAt(),
                run.getCreatedAt()
        );
    }
}


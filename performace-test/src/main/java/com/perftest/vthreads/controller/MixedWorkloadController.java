package com.perftest.vthreads.controller;

import com.perftest.vthreads.model.WorkloadResponse;
import com.perftest.vthreads.service.MixedWorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mixed")
public class MixedWorkloadController {

    private static final Logger log = LoggerFactory.getLogger(MixedWorkloadController.class);

    private final MixedWorkloadService mixedWorkloadService;

    public MixedWorkloadController(MixedWorkloadService mixedWorkloadService) {
        this.mixedWorkloadService = mixedWorkloadService;
    }

    /**
     * GET /api/mixed/realistic?dbDelayMs=50&httpDelayMs=100&cpuIterations=10000
     *
     * Executes a realistic three-phase workload that mimics a typical request handler:
     *
     *   Phase 1 - DB I/O  : query the database with artificial latency (dbDelayMs)
     *   Phase 2 - HTTP I/O: call a downstream service (httpDelayMs)
     *   Phase 3 - CPU work: compute primes up to cpuIterations
     *
     * Default values are deliberately modest so a developer can test quickly without
     * overloading their machine. For serious benchmarking, increase the values:
     *   - dbDelayMs=100, httpDelayMs=200 to stress the I/O advantage
     *   - cpuIterations=500000 to add meaningful CPU pressure
     *
     * Response includes per-phase timing so you can see exactly where time is spent.
     */
    @GetMapping("/realistic")
    public ResponseEntity<WorkloadResponse> realisticWorkload(
            @RequestParam(defaultValue = "50")    long dbDelayMs,
            @RequestParam(defaultValue = "100")   long httpDelayMs,
            @RequestParam(defaultValue = "10000") long cpuIterations) throws Exception {

        Thread current = Thread.currentThread();
        long start = System.currentTimeMillis();

        Map<String, Object> phaseResults = mixedWorkloadService.realisticWorkload(
                dbDelayMs, httpDelayMs, cpuIterations);

        long totalDuration = System.currentTimeMillis() - start;

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("requestedDbDelayMs",    dbDelayMs);
        metadata.put("requestedHttpDelayMs",   httpDelayMs);
        metadata.put("requestedCpuIterations", cpuIterations);
        metadata.put("totalDurationMs",        totalDuration);
        metadata.putAll(phaseResults);

        WorkloadResponse response = new WorkloadResponse(
                "/api/mixed/realistic",
                totalDuration,
                current.getName(),
                current.isVirtual(),
                Thread.activeCount(),
                metadata
        );

        log.debug("realisticWorkload: totalMs={}, dbMs={}, httpMs={}, cpuMs={}, thread={}, isVirtual={}",
                totalDuration,
                phaseResults.get("dbPhaseMs"),
                phaseResults.get("httpPhaseMs"),
                phaseResults.get("cpuPhaseMs"),
                current.getName(),
                current.isVirtual());

        return ResponseEntity.ok(response);
    }
}

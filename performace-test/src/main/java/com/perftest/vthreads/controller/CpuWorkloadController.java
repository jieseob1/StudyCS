package com.perftest.vthreads.controller;

import com.perftest.vthreads.model.WorkloadResponse;
import com.perftest.vthreads.service.CpuSimulationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cpu")
public class CpuWorkloadController {

    private static final Logger log = LoggerFactory.getLogger(CpuWorkloadController.class);

    private final CpuSimulationService cpuSimulationService;

    public CpuWorkloadController(CpuSimulationService cpuSimulationService) {
        this.cpuSimulationService = cpuSimulationService;
    }

    /**
     * GET /api/cpu/compute?iterations=1000000
     *
     * Computes all prime numbers up to {@code iterations} using trial division.
     *
     * This endpoint intentionally demonstrates that virtual threads provide NO throughput
     * advantage for pure CPU work, because the carrier thread remains occupied for the
     * full computation duration. Under high concurrency with virtual threads, the fixed
     * carrier pool (= number of CPU cores) becomes the bottleneck.
     */
    @GetMapping("/compute")
    public ResponseEntity<WorkloadResponse> computePrimes(
            @RequestParam(defaultValue = "1000000") long iterations) {

        Thread current = Thread.currentThread();
        long start = System.currentTimeMillis();

        List<Long> primes = cpuSimulationService.computePrimes(iterations);

        long duration = System.currentTimeMillis() - start;

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("iterations", iterations);
        metadata.put("primesFound", primes.size());
        metadata.put("largestPrime", primes.isEmpty() ? null : primes.getLast());
        metadata.put("durationMs", duration);

        WorkloadResponse response = new WorkloadResponse(
                "/api/cpu/compute",
                duration,
                current.getName(),
                current.isVirtual(),
                Thread.activeCount(),
                metadata
        );

        log.debug("computePrimes: iterations={}, primesFound={}, durationMs={}, thread={}, isVirtual={}",
                iterations, primes.size(), duration, current.getName(), current.isVirtual());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/cpu/hash?payload=hello&rounds=100
     *
     * Repeatedly hashes the given payload using SHA-256 for the specified number of rounds.
     * Each round feeds the previous output as input, creating a chain of hashes.
     *
     * Use this to simulate cryptographic operations (e.g., password hashing, signing).
     * Like prime computation, this is CPU-bound and will not benefit from virtual threads
     * at the individual-request level.
     */
    @GetMapping("/hash")
    public ResponseEntity<WorkloadResponse> hashPayload(
            @RequestParam(defaultValue = "hello") String payload,
            @RequestParam(defaultValue = "100") int rounds) {

        Thread current = Thread.currentThread();
        long start = System.currentTimeMillis();

        String finalHash = cpuSimulationService.hashPayload(payload, rounds);

        long duration = System.currentTimeMillis() - start;

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("payloadLength", payload.length());
        metadata.put("rounds", rounds);
        metadata.put("finalHash", finalHash);
        metadata.put("durationMs", duration);

        WorkloadResponse response = new WorkloadResponse(
                "/api/cpu/hash",
                duration,
                current.getName(),
                current.isVirtual(),
                Thread.activeCount(),
                metadata
        );

        log.debug("hashPayload: rounds={}, durationMs={}, thread={}, isVirtual={}",
                rounds, duration, current.getName(), current.isVirtual());
        return ResponseEntity.ok(response);
    }
}

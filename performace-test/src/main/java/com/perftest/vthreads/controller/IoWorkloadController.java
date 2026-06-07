package com.perftest.vthreads.controller;

import com.perftest.vthreads.model.WorkloadResponse;
import com.perftest.vthreads.service.IoSimulationService;
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
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/io")
public class IoWorkloadController {

    private static final Logger log = LoggerFactory.getLogger(IoWorkloadController.class);

    private final IoSimulationService ioSimulationService;

    public IoWorkloadController(IoSimulationService ioSimulationService) {
        this.ioSimulationService = ioSimulationService;
    }

    /**
     * GET /api/io/sleep?ms=500
     *
     * Simplest I/O simulation: park the thread for the given number of milliseconds.
     * Under virtual threads, the carrier thread is freed during the sleep; under platform
     * threads, the OS thread is parked for the full duration.
     */
    @GetMapping("/sleep")
    public ResponseEntity<WorkloadResponse> sleep(
            @RequestParam(defaultValue = "500") long ms) throws InterruptedException {

        Thread current = Thread.currentThread();
        long start = System.currentTimeMillis();

        ioSimulationService.simulateSleep(ms);

        long duration = System.currentTimeMillis() - start;
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("requestedSleepMs", ms);
        metadata.put("actualDurationMs", duration);

        WorkloadResponse response = new WorkloadResponse(
                "/api/io/sleep",
                duration,
                current.getName(),
                current.isVirtual(),
                Thread.activeCount(),
                metadata
        );

        log.debug("sleep completed: durationMs={}, thread={}, isVirtual={}",
                duration, current.getName(), current.isVirtual());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/io/db-query?delayMs=200
     *
     * Simulates a database query: executes a real query against H2 (touching the connection
     * pool) then sleeps to simulate slow-query latency.
     */
    @GetMapping("/db-query")
    public ResponseEntity<WorkloadResponse> dbQuery(
            @RequestParam(defaultValue = "200") long delayMs) throws InterruptedException {

        Thread current = Thread.currentThread();
        long start = System.currentTimeMillis();

        ioSimulationService.simulateDbQuery(delayMs);

        long duration = System.currentTimeMillis() - start;
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("simulatedDbDelayMs", delayMs);
        metadata.put("actualDurationMs", duration);
        metadata.put("note", "SELECT 1 + Thread.sleep to simulate DB latency");

        WorkloadResponse response = new WorkloadResponse(
                "/api/io/db-query",
                duration,
                current.getName(),
                current.isVirtual(),
                Thread.activeCount(),
                metadata
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/io/http-call?delayMs=300
     *
     * Simulates an outbound HTTP call by invoking /api/io/sleep on self with a blocking
     * RestClient. The key test: does the calling thread block or yield?
     */
    @GetMapping("/http-call")
    public ResponseEntity<WorkloadResponse> httpCall(
            @RequestParam(defaultValue = "300") long delayMs) {

        Thread current = Thread.currentThread();
        long start = System.currentTimeMillis();

        ioSimulationService.simulateHttpCall(delayMs);

        long duration = System.currentTimeMillis() - start;
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("simulatedHttpDelayMs", delayMs);
        metadata.put("actualDurationMs", duration);
        metadata.put("target", "self /api/io/sleep");

        WorkloadResponse response = new WorkloadResponse(
                "/api/io/http-call",
                duration,
                current.getName(),
                current.isVirtual(),
                Thread.activeCount(),
                metadata
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/io/fan-out?calls=5&delayMs=200
     *
     * Fires N concurrent blocking HTTP calls in parallel.
     * Returns the outer response with aggregate timing plus sub-task details.
     *
     * Virtual threads: all N calls happen on virtual threads; the carrier thread pool
     *   is not exhausted regardless of N.
     * Platform threads: N real OS threads are consumed for the duration of the calls.
     */
    @GetMapping("/fan-out")
    public ResponseEntity<WorkloadResponse> fanOut(
            @RequestParam(defaultValue = "5") int calls,
            @RequestParam(defaultValue = "200") long delayMs) throws InterruptedException, ExecutionException {

        Thread current = Thread.currentThread();
        long start = System.currentTimeMillis();

        List<WorkloadResponse> subResults = ioSimulationService.simulateFanOut(calls, delayMs);

        long duration = System.currentTimeMillis() - start;

        // Summarise sub-task thread info
        long virtualCount = subResults.stream().filter(WorkloadResponse::isVirtualThread).count();
        long platformCount = subResults.size() - virtualCount;

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("requestedCalls", calls);
        metadata.put("completedCalls", subResults.size());
        metadata.put("simulatedDelayMs", delayMs);
        metadata.put("actualDurationMs", duration);
        metadata.put("subTaskVirtualThreads", virtualCount);
        metadata.put("subTaskPlatformThreads", platformCount);
        metadata.put("subResults", subResults.stream()
                .map(r -> Map.of(
                        "endpoint", r.endpoint(),
                        "durationMs", r.durationMs(),
                        "threadName", r.threadName(),
                        "isVirtual", r.isVirtualThread()
                ))
                .toList());

        WorkloadResponse response = new WorkloadResponse(
                "/api/io/fan-out",
                duration,
                current.getName(),
                current.isVirtual(),
                Thread.activeCount(),
                metadata
        );

        log.debug("fan-out completed: calls={}, durationMs={}, virtualSubTasks={}, platformSubTasks={}",
                calls, duration, virtualCount, platformCount);
        return ResponseEntity.ok(response);
    }
}

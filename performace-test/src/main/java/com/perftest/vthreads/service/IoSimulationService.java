package com.perftest.vthreads.service;

import com.perftest.vthreads.model.WorkloadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class IoSimulationService {

    private static final Logger log = LoggerFactory.getLogger(IoSimulationService.class);

    private final JdbcTemplate jdbcTemplate;
    private final RestClient selfRestClient;

    @Value("${spring.threads.virtual.enabled:false}")
    private boolean virtualThreadsEnabled;

    public IoSimulationService(
            JdbcTemplate jdbcTemplate,
            @Qualifier("selfRestClient") RestClient selfRestClient) {
        this.jdbcTemplate = jdbcTemplate;
        this.selfRestClient = selfRestClient;
    }

    /**
     * Simulates I/O latency via Thread.sleep.
     * With virtual threads, this yields the carrier thread rather than blocking it.
     */
    public void simulateSleep(long ms) throws InterruptedException {
        Thread current = Thread.currentThread();
        log.debug("simulateSleep: thread={}, isVirtual={}, sleepMs={}",
                current.getName(), current.isVirtual(), ms);
        Thread.sleep(ms);
    }

    /**
     * Simulates a database query with artificial latency.
     *
     * H2 does not have a native SLEEP function, so we:
     *  1. Actually touch the DB connection pool by executing a real query (SELECT 1).
     *  2. Use Thread.sleep to simulate the round-trip latency a slow query would incur.
     *
     * This accurately tests how the thread model handles DB I/O blocking:
     * - Platform threads: the OS thread is parked for the full duration.
     * - Virtual threads: the virtual thread is unmounted from its carrier thread,
     *   freeing the carrier to run other virtual threads while "waiting".
     */
    public void simulateDbQuery(long delayMs) throws InterruptedException {
        Thread current = Thread.currentThread();
        log.debug("simulateDbQuery: thread={}, isVirtual={}, delayMs={}",
                current.getName(), current.isVirtual(), delayMs);

        // Touch the actual DB connection pool with a lightweight query
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        log.debug("DB ping result: {}", result);

        // Simulate the latency of a real (potentially slow) query
        Thread.sleep(delayMs);
    }

    /**
     * Simulates an outbound HTTP call by calling the self /api/io/sleep endpoint.
     * This is a blocking RestClient call - ideal for demonstrating virtual thread advantage.
     *
     * Platform threads: OS thread blocks for the full HTTP round-trip.
     * Virtual threads: virtual thread is unmounted while waiting for the HTTP response,
     *                  freeing the carrier to serve other requests.
     */
    public void simulateHttpCall(long delayMs) {
        Thread current = Thread.currentThread();
        log.debug("simulateHttpCall: thread={}, isVirtual={}, delayMs={}",
                current.getName(), current.isVirtual(), delayMs);

        // Blocking HTTP call to self - tests I/O blocking behaviour under load
        WorkloadResponse response = selfRestClient
                .get()
                .uri("/api/io/sleep?ms={ms}", delayMs)
                .retrieve()
                .body(WorkloadResponse.class);

        log.debug("HTTP call completed, remote thread was: {}",
                response != null ? response.threadName() : "unknown");
    }

    /**
     * Fan-out: fires N concurrent blocking calls in parallel using an executor appropriate
     * for the current thread mode.
     *
     * - Virtual threads enabled : uses Executors.newVirtualThreadPerTaskExecutor()
     *   Each call gets its own virtual thread, so N concurrent sleeps cost only N virtual threads
     *   (no platform thread pool exhaustion).
     *
     * - Platform threads         : uses a cached thread pool
     *   Each call needs a real OS thread; under high concurrency this can exhaust the pool.
     *
     * The executor is created fresh per call so that the benchmark shows each strategy cleanly.
     */
    public List<WorkloadResponse> simulateFanOut(int calls, long delayMs)
            throws InterruptedException, ExecutionException {

        Thread current = Thread.currentThread();
        log.debug("simulateFanOut: thread={}, isVirtual={}, calls={}, delayMs={}",
                current.getName(), current.isVirtual(), calls, delayMs);

        ExecutorService executor = virtualThreadsEnabled
                ? Executors.newVirtualThreadPerTaskExecutor()
                : Executors.newCachedThreadPool();

        List<Callable<WorkloadResponse>> tasks = new ArrayList<>(calls);
        for (int i = 0; i < calls; i++) {
            final int taskIndex = i;
            tasks.add(() -> {
                long start = System.currentTimeMillis();
                Thread t = Thread.currentThread();
                log.debug("fan-out task {}: thread={}, isVirtual={}", taskIndex, t.getName(), t.isVirtual());

                // Each task does a blocking HTTP call to simulate a real fan-out scenario
                WorkloadResponse response = selfRestClient
                        .get()
                        .uri("/api/io/sleep?ms={ms}", delayMs)
                        .retrieve()
                        .body(WorkloadResponse.class);

                long elapsed = System.currentTimeMillis() - start;
                return new WorkloadResponse(
                        "/api/io/fan-out/task-" + taskIndex,
                        elapsed,
                        t.getName(),
                        t.isVirtual(),
                        Thread.activeCount(),
                        Map.of(
                                "taskIndex", taskIndex,
                                "remoteDurationMs", response != null ? response.durationMs() : -1L
                        )
                );
            });
        }

        try {
            List<Future<WorkloadResponse>> futures = executor.invokeAll(tasks);
            List<WorkloadResponse> results = new ArrayList<>(calls);
            for (Future<WorkloadResponse> future : futures) {
                results.add(future.get());
            }
            return results;
        } finally {
            executor.shutdown();
        }
    }
}

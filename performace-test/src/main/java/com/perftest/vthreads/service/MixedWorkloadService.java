package com.perftest.vthreads.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MixedWorkloadService {

    private static final Logger log = LoggerFactory.getLogger(MixedWorkloadService.class);

    private final IoSimulationService ioSimulationService;
    private final CpuSimulationService cpuSimulationService;

    public MixedWorkloadService(
            IoSimulationService ioSimulationService,
            CpuSimulationService cpuSimulationService) {
        this.ioSimulationService = ioSimulationService;
        this.cpuSimulationService = cpuSimulationService;
    }

    /**
     * Realistic mixed workload that combines:
     *  1. DB I/O   - simulates fetching data from a database (blocking, yields virtual thread)
     *  2. HTTP I/O - simulates calling a downstream service   (blocking, yields virtual thread)
     *  3. CPU work - simulates business logic / computation   (keeps carrier busy)
     *
     * This sequence mirrors a typical request handler that:
     *   - Queries the DB for user/session data
     *   - Calls an external API (e.g. payment, notification)
     *   - Does some local computation on the results
     *
     * Under virtual threads the I/O steps release the carrier thread, allowing other
     * virtual threads to run during the wait periods. The CPU step is intentionally brief
     * so it does not negate the I/O advantage.
     *
     * @param dbDelayMs      artificial DB query latency in milliseconds
     * @param httpDelayMs    artificial HTTP call latency in milliseconds
     * @param cpuIterations  upper bound for prime search (controls CPU intensity)
     * @return metadata map describing what was done and timing per phase
     */
    public Map<String, Object> realisticWorkload(long dbDelayMs, long httpDelayMs, long cpuIterations)
            throws Exception {

        Thread current = Thread.currentThread();
        log.debug("realisticWorkload: thread={}, isVirtual={}, dbDelay={}, httpDelay={}, cpuIter={}",
                current.getName(), current.isVirtual(), dbDelayMs, httpDelayMs, cpuIterations);

        // --- Phase 1: DB I/O ---
        long dbStart = System.currentTimeMillis();
        ioSimulationService.simulateDbQuery(dbDelayMs);
        long dbDuration = System.currentTimeMillis() - dbStart;
        log.debug("realisticWorkload phase1 DB done in {}ms", dbDuration);

        // --- Phase 2: HTTP I/O ---
        long httpStart = System.currentTimeMillis();
        ioSimulationService.simulateHttpCall(httpDelayMs);
        long httpDuration = System.currentTimeMillis() - httpStart;
        log.debug("realisticWorkload phase2 HTTP done in {}ms", httpDuration);

        // --- Phase 3: CPU computation ---
        long cpuStart = System.currentTimeMillis();
        List<Long> primes = cpuSimulationService.computePrimes(cpuIterations);
        long cpuDuration = System.currentTimeMillis() - cpuStart;
        log.debug("realisticWorkload phase3 CPU done in {}ms, primes={}", cpuDuration, primes.size());

        return Map.of(
                "dbPhaseMs",       dbDuration,
                "httpPhaseMs",     httpDuration,
                "cpuPhaseMs",      cpuDuration,
                "totalPhaseMs",    dbDuration + httpDuration + cpuDuration,
                "primesFound",     primes.size(),
                "primesUpperBound", cpuIterations,
                "thread",          current.getName(),
                "isVirtual",       current.isVirtual()
        );
    }
}

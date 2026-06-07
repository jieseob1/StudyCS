package com.perftest.vthreads.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/diagnostics")
public class DiagnosticsController {

    private static final Logger log = LoggerFactory.getLogger(DiagnosticsController.class);

    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    /**
     * GET /api/diagnostics/thread-info
     *
     * Returns information about the thread handling this very request.
     * The most important fields to observe are:
     *  - threadName   : shows "virtual-X" vs "http-nio-8080-exec-N" naming patterns
     *  - isVirtual    : true when Spring Boot is running with virtual threads enabled
     *  - threadId     : unique ID for this thread
     *
     * Use this endpoint during a load test to confirm that requests are actually
     * being served by virtual (or platform) threads as expected.
     */
    @GetMapping("/thread-info")
    public ResponseEntity<Map<String, Object>> threadInfo() {
        Thread current = Thread.currentThread();

        Map<String, Object> info = new HashMap<>();
        info.put("threadName",    current.getName());
        info.put("threadId",      current.threadId());
        info.put("isVirtual",     current.isVirtual());
        info.put("isDaemon",      current.isDaemon());
        info.put("state",         current.getState().name());
        info.put("priority",      current.getPriority());
        info.put("threadGroup",   current.isVirtual() ? "virtual" : current.getThreadGroup().getName());
        info.put("activeCount",   Thread.activeCount());

        log.debug("thread-info requested: name={}, id={}, isVirtual={}",
                current.getName(), current.threadId(), current.isVirtual());

        return ResponseEntity.ok(info);
    }

    /**
     * GET /api/diagnostics/thread-count
     *
     * Returns JVM-level thread statistics from ThreadMXBean.
     * Key metrics to watch during a load test:
     *
     *  - liveThreadCount : current number of live platform threads
     *                      (virtual threads do NOT appear here; they are not OS threads)
     *  - peakThreadCount : maximum live platform threads since JVM start or last reset
     *  - daemonThreadCount : number of daemon threads
     *  - deadlockedThreads : thread IDs involved in a deadlock (should always be empty)
     *
     * When using virtual threads, peakThreadCount will remain low even under heavy load,
     * because thousands of virtual threads share a small number of carrier (platform) threads.
     * Under platform threads the live/peak counts will climb with concurrency.
     */
    @GetMapping("/thread-count")
    public ResponseEntity<Map<String, Object>> threadCount() {
        int liveCount      = threadMXBean.getThreadCount();
        int peakCount      = threadMXBean.getPeakThreadCount();
        int daemonCount    = threadMXBean.getDaemonThreadCount();
        long totalStarted  = threadMXBean.getTotalStartedThreadCount();
        long[] deadlocked  = threadMXBean.findDeadlockedThreads();

        // Collect names of all current platform threads for visibility
        List<String> threadNames = Arrays.stream(threadMXBean.dumpAllThreads(false, false))
                .map(ThreadInfo::getThreadName)
                .sorted()
                .toList();

        Map<String, Object> stats = new HashMap<>();
        stats.put("liveThreadCount",   liveCount);
        stats.put("peakThreadCount",   peakCount);
        stats.put("daemonThreadCount", daemonCount);
        stats.put("totalStarted",      totalStarted);
        stats.put("deadlockedThreadIds",
                deadlocked != null ? Arrays.stream(deadlocked).boxed().toList() : List.of());
        stats.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        stats.put("platformThreadNames", threadNames);
        stats.put("platformThreadCount", threadNames.size());
        stats.put("note",
                "Virtual threads are NOT counted in liveThreadCount / peakThreadCount. " +
                "They are lightweight and do not appear as OS-level threads in ThreadMXBean.");

        return ResponseEntity.ok(stats);
    }
}

package com.perftest.vthreads.controller;

import com.perftest.vthreads.model.VirtualThreadTraceResponse;
import com.perftest.vthreads.service.VirtualThreadTraceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/virtual-threads")
public class VirtualThreadTraceController {

    private final VirtualThreadTraceService virtualThreadTraceService;

    public VirtualThreadTraceController(VirtualThreadTraceService virtualThreadTraceService) {
        this.virtualThreadTraceService = virtualThreadTraceService;
    }

    /**
     * GET /api/virtual-threads/lifecycle
     *
     * Creates virtual threads explicitly from application code and returns a timeline of
     * their state changes. This is separate from Spring's request-thread mode: even when
     * spring.threads.virtual.enabled=false, this endpoint still uses Thread.ofVirtual().
     */
    @GetMapping("/lifecycle")
    public ResponseEntity<VirtualThreadTraceResponse> lifecycle(
            @RequestParam(defaultValue = "3") int tasks,
            @RequestParam(defaultValue = "300") long parkMs,
            @RequestParam(defaultValue = "25") long monitorIntervalMs,
            @RequestParam(defaultValue = "20") long cpuMs) throws InterruptedException {

        return ResponseEntity.ok(virtualThreadTraceService.traceLifecycle(
                tasks,
                parkMs,
                monitorIntervalMs,
                cpuMs
        ));
    }
}

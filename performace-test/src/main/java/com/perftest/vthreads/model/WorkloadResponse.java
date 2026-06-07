package com.perftest.vthreads.model;

import java.util.Map;

public record WorkloadResponse(
        String endpoint,
        long durationMs,
        String threadName,
        boolean isVirtualThread,
        long activeThreadCount,
        Map<String, Object> metadata
) {}

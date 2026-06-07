package com.perftest.vthreads.model;

import java.util.List;
import java.util.Map;

public record VirtualThreadTraceResponse(
        boolean springVirtualThreadsEnabled,
        String executionModel,
        ThreadSnapshot requestThread,
        int taskCount,
        long parkMs,
        long monitorIntervalMs,
        long cpuMs,
        List<LifecycleEvent> events,
        List<StateSample> samples,
        Map<String, Object> summary
) {

    public record ThreadSnapshot(
            String name,
            long id,
            boolean virtual,
            String state,
            boolean alive,
            String description
    ) {
    }

    public record LifecycleEvent(
            long sequence,
            long elapsedMs,
            String phase,
            String taskName,
            ThreadSnapshot thread,
            String note
    ) {
    }

    public record StateSample(
            long elapsedMs,
            String label,
            Map<String, String> threadStates,
            long aliveCount
    ) {
    }
}

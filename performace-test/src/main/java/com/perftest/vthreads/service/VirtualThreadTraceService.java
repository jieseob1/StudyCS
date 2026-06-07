package com.perftest.vthreads.service;

import com.perftest.vthreads.model.VirtualThreadTraceResponse;
import com.perftest.vthreads.model.VirtualThreadTraceResponse.LifecycleEvent;
import com.perftest.vthreads.model.VirtualThreadTraceResponse.StateSample;
import com.perftest.vthreads.model.VirtualThreadTraceResponse.ThreadSnapshot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class VirtualThreadTraceService {

    private static final int MAX_TASKS = 100;
    private static final long MAX_PARK_MS = 5_000;
    private static final long MAX_CPU_MS = 1_000;

    private final boolean springVirtualThreadsEnabled;

    public VirtualThreadTraceService(
            @Value("${spring.threads.virtual.enabled:false}") boolean springVirtualThreadsEnabled) {
        this.springVirtualThreadsEnabled = springVirtualThreadsEnabled; // 가상 스레드 사용 여부
    }

    public VirtualThreadTraceResponse traceLifecycle(
            int requestedTasks,
            long requestedParkMs,
            long requestedMonitorIntervalMs,
            long requestedCpuMs) throws InterruptedException {

        int taskCount = clamp(requestedTasks, 1, MAX_TASKS);
        long parkMs = clamp(requestedParkMs, 1, MAX_PARK_MS);
        long monitorIntervalMs = clamp(requestedMonitorIntervalMs, 1, 500);
        long cpuMs = clamp(requestedCpuMs, 0, MAX_CPU_MS);

        long startedAt = System.nanoTime();
        Thread requestThread = Thread.currentThread();
        List<LifecycleEvent> events = java.util.Collections.synchronizedList(new ArrayList<>());
        List<StateSample> samples = new ArrayList<>();
        AtomicLong sequence = new AtomicLong();
        CountDownLatch readyGate = new CountDownLatch(taskCount);
        CountDownLatch releaseGate = new CountDownLatch(1);
        CountDownLatch sleepingGate = new CountDownLatch(taskCount);
        List<Thread> taskThreads = new ArrayList<>(taskCount);

        Thread.Builder.OfVirtual builder = Thread.ofVirtual().name("lab-vt-", 0);
        for (int i = 0; i < taskCount; i++) {
            String taskName = "task-" + i;
            Thread thread = builder.unstarted(() -> runLifecycleTask(
                    taskName,
                    parkMs,
                    cpuMs,
                    readyGate,
                    releaseGate,
                    sleepingGate,
                    events,
                    sequence,
                    startedAt
            ));
            taskThreads.add(thread);
            recordEvent(events, sequence, startedAt, "created", taskName, thread,
                    "Thread.ofVirtual().unstarted(...) created a virtual thread object; start() has not run yet.");
        }

        taskThreads.forEach(Thread::start);
        if (!readyGate.await(2, TimeUnit.SECONDS)) {
            recordSyntheticEvent(events, sequence, startedAt, "timeout", "main",
                    requestThread, "Not every task reached the start gate before timeout.");
        }

        waitForObservedState(taskThreads, Thread.State.WAITING, 500, monitorIntervalMs,
                samples, startedAt, "after-start-before-release");

        releaseGate.countDown();
        sleepingGate.await(Math.max(500, parkMs + 500), TimeUnit.MILLISECONDS);
        waitForObservedState(taskThreads, Thread.State.TIMED_WAITING, Math.max(500, parkMs),
                monitorIntervalMs, samples, startedAt, "during-thread-sleep");

        while (taskThreads.stream().anyMatch(Thread::isAlive)) {
            samples.add(sampleThreads(taskThreads, startedAt, "monitor-tick"));
            Thread.sleep(monitorIntervalMs);
        }

        for (Thread thread : taskThreads) {
            thread.join();
        }

        samples.add(sampleThreads(taskThreads, startedAt, "after-join"));
        for (int i = 0; i < taskThreads.size(); i++) {
            recordEvent(events, sequence, startedAt, "terminated", "task-" + i, taskThreads.get(i),
                    "join() returned; the virtual thread has reached TERMINATED.");
        }

        List<LifecycleEvent> orderedEvents = events.stream()
                .sorted(Comparator.comparingLong(LifecycleEvent::sequence))
                .toList();
        Map<String, Object> summary = summarize(taskThreads, orderedEvents, samples, parkMs, cpuMs);

        return new VirtualThreadTraceResponse(
                springVirtualThreadsEnabled,
                "application-created virtual threads via Thread.ofVirtual(); independent of the Spring request thread mode",
                snapshot(requestThread),
                taskCount,
                parkMs,
                monitorIntervalMs,
                cpuMs,
                orderedEvents,
                samples,
                summary
        );
    }

    private void runLifecycleTask(
            String taskName,
            long parkMs,
            long cpuMs,
            CountDownLatch readyGate,
            CountDownLatch releaseGate,
            CountDownLatch sleepingGate,
            List<LifecycleEvent> events,
            AtomicLong sequence,
            long startedAt) {

        Thread current = Thread.currentThread();
        try {
            recordEvent(events, sequence, startedAt, "started", taskName, current,
                    "run() is executing on a virtual thread.");
            readyGate.countDown();
            recordEvent(events, sequence, startedAt, "waiting-on-start-gate", taskName, current,
                    "CountDownLatch.await() parks the virtual thread until the controller releases it.");
            releaseGate.await();
            recordEvent(events, sequence, startedAt, "released", taskName, current,
                    "The virtual thread resumed after the latch was released.");
            if (cpuMs > 0) {
                recordEvent(events, sequence, startedAt, "cpu-work", taskName, current,
                        "CPU work keeps the virtual thread mounted while it is runnable.");
                burnCpuFor(cpuMs);
            }
            recordEvent(events, sequence, startedAt, "before-sleep", taskName, current,
                    "Thread.sleep(...) will park this virtual thread and free its carrier while waiting.");
            sleepingGate.countDown();
            Thread.sleep(parkMs);
            recordEvent(events, sequence, startedAt, "after-sleep", taskName, current,
                    "The virtual thread resumed after timed parking.");
            recordEvent(events, sequence, startedAt, "finishing", taskName, current,
                    "The task is about to return from run().");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            recordEvent(events, sequence, startedAt, "interrupted", taskName, current,
                    "The virtual thread was interrupted while parked.");
        }
    }

    private void waitForObservedState(
            List<Thread> threads,
            Thread.State expectedState,
            long timeoutMs,
            long monitorIntervalMs,
            List<StateSample> samples,
            long startedAt,
            String labelPrefix) throws InterruptedException {

        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMs);
        int attempt = 0;
        while (System.nanoTime() < deadline) {
            StateSample sample = sampleThreads(threads, startedAt, labelPrefix + "-" + attempt);
            samples.add(sample);
            if (sample.threadStates().containsValue(expectedState.name())) {
                return;
            }
            Thread.sleep(monitorIntervalMs);
            attempt++;
        }
    }

    private StateSample sampleThreads(List<Thread> threads, long startedAt, String label) {
        Map<String, String> threadStates = new LinkedHashMap<>();
        long aliveCount = 0;
        for (Thread thread : threads) {
            threadStates.put(thread.getName(), thread.getState().name());
            if (thread.isAlive()) {
                aliveCount++;
            }
        }
        return new StateSample(elapsedMs(startedAt), label, threadStates, aliveCount);
    }

    private Map<String, Object> summarize(
            List<Thread> taskThreads,
            List<LifecycleEvent> events,
            List<StateSample> samples,
            long parkMs,
            long cpuMs) {

        Set<String> observedStates = new LinkedHashSet<>();
        for (StateSample sample : samples) {
            observedStates.addAll(sample.threadStates().values());
        }
        events.stream()
                .map(event -> event.thread().state())
                .forEach(observedStates::add);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("allTaskThreadsVirtual", taskThreads.stream().allMatch(Thread::isVirtual));
        summary.put("observedStates", observedStates);
        summary.put("waitingObserved", observedStates.contains(Thread.State.WAITING.name()));
        summary.put("timedWaitingObserved", observedStates.contains(Thread.State.TIMED_WAITING.name()));
        summary.put("terminatedObserved", observedStates.contains(Thread.State.TERMINATED.name()));
        summary.put("virtualThreadCount", taskThreads.size());
        summary.put("parkMs", parkMs);
        summary.put("cpuMs", cpuMs);
        summary.put("phaseOrder", events.stream().map(LifecycleEvent::phase).distinct().toList());
        summary.put("note",
                "Thread.State shows Java-level state. Carrier platform thread mount/unmount is managed by the JVM scheduler.");
        return summary;
    }

    private void recordSyntheticEvent(
            List<LifecycleEvent> events,
            AtomicLong sequence,
            long startedAt,
            String phase,
            String taskName,
            Thread thread,
            String note) {
        recordEvent(events, sequence, startedAt, phase, taskName, thread, note);
    }

    private void recordEvent(
            List<LifecycleEvent> events,
            AtomicLong sequence,
            long startedAt,
            String phase,
            String taskName,
            Thread thread,
            String note) {
        events.add(new LifecycleEvent(
                sequence.incrementAndGet(),
                elapsedMs(startedAt),
                phase,
                taskName,
                snapshot(thread),
                note
        ));
    }

    private ThreadSnapshot snapshot(Thread thread) {
        return new ThreadSnapshot(
                thread.getName(),
                thread.threadId(),
                thread.isVirtual(),
                thread.getState().name(),
                thread.isAlive(),
                thread.toString()
        );
    }

    private long elapsedMs(long startedAt) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
    }

    private void burnCpuFor(long cpuMs) {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(cpuMs);
        long value = 0;
        while (System.nanoTime() < deadline) {
            value += System.nanoTime() & 0xFF;
        }
        if (value == Long.MIN_VALUE) {
            throw new IllegalStateException("unreachable");
        }
    }

    private int clamp(int value, int min, int max) {
        // 해당 기능은 입력값이 min과 max 사이에 있도록 보장하는 유틸리티 함수입니다.
        // 만약 value가 min보다 작으면 min을 반환하고, value가 max보다 크면 max를 반환합니다.
        // 그렇지 않으면 value를 그대로 반환합니다.
        return Math.min(Math.max(value, min), max);
    }

    private long clamp(long value, long min, long max) {
        return Math.min(Math.max(value, min), max);
    }
}

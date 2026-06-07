package com.perftest.vthreads.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Registers custom Micrometer gauges that expose JVM thread statistics to Prometheus.
 *
 * Gauges registered:
 *  - jvm.threads.platform.live  : current number of live platform threads
 *  - jvm.threads.peak           : peak live platform thread count since JVM start
 *
 * Note: Micrometer already ships jvm.threads.live and jvm.threads.peak via its built-in
 * JvmThreadMetrics binder (auto-configured by Spring Boot Actuator). These custom gauges
 * use explicit names to ensure they appear in Prometheus output even if the built-in binder
 * is not on the classpath, and to demonstrate custom metric registration.
 *
 * During a benchmark, watch these metrics to observe:
 *  - Platform thread mode : liveCount climbs with concurrency, up to server.tomcat.threads.max
 *  - Virtual thread mode  : liveCount stays low (only carrier threads = CPU cores count)
 */
@Component
public class ThreadMetricsExporter {

    private static final Logger log = LoggerFactory.getLogger(ThreadMetricsExporter.class);

    private final MeterRegistry meterRegistry;
    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    public ThreadMetricsExporter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void registerMetrics() {
        // ----------------------------------------------------------------
        // jvm.threads.platform.live
        // Current number of live platform threads.
        // Virtual threads are NOT counted here; they are JVM-managed
        // lightweight threads that do not correspond to OS threads.
        // ----------------------------------------------------------------
        Gauge.builder("jvm.threads.platform.live", threadMXBean, ThreadMXBean::getThreadCount)
                .description("Current number of live platform (OS) threads in the JVM. " +
                             "Virtual threads are excluded.")
                .tag("type", "platform")
                .register(meterRegistry);

        // ----------------------------------------------------------------
        // jvm.threads.peak
        // Peak live platform thread count since JVM start (or last reset).
        // Under platform thread mode this will reflect max concurrency seen.
        // Under virtual thread mode it will stay near the baseline (carrier count).
        // ----------------------------------------------------------------
        Gauge.builder("jvm.threads.peak", threadMXBean, ThreadMXBean::getPeakThreadCount)
                .description("Peak number of live platform threads since JVM start or peak reset.")
                .tag("type", "peak")
                .register(meterRegistry);

        // ----------------------------------------------------------------
        // jvm.threads.daemon
        // Number of active daemon threads. Useful for sanity-checking that
        // background infrastructure threads (GC, finalizer, etc.) are healthy.
        // ----------------------------------------------------------------
        Gauge.builder("jvm.threads.daemon.custom", threadMXBean, ThreadMXBean::getDaemonThreadCount)
                .description("Current number of live daemon platform threads.")
                .tag("type", "daemon")
                .register(meterRegistry);

        // ----------------------------------------------------------------
        // jvm.threads.started.total
        // Monotonically increasing counter of all threads ever started.
        // Under virtual thread mode this can grow very quickly (one virtual
        // thread per request) but platform thread count remains stable.
        // ----------------------------------------------------------------
        Gauge.builder("jvm.threads.started.total", threadMXBean, ThreadMXBean::getTotalStartedThreadCount)
                .description("Total number of threads started since JVM launch (includes terminated threads).")
                .tag("type", "total-started")
                .register(meterRegistry);

        log.info("ThreadMetricsExporter: registered custom gauges " +
                 "[jvm.threads.platform.live, jvm.threads.peak, jvm.threads.daemon.custom, jvm.threads.started.total]");
    }
}

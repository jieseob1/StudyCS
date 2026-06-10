package com.studycs.webfluxdemo.support

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.time.Instant

object DemoLog {
    private val log = LoggerFactory.getLogger("com.studycs.webfluxdemo.DemoTrace")

    fun threadName(): String = Thread.currentThread().name

    fun now(): String = Instant.now().toString()

    fun mark(
        requestId: String,
        layer: String,
        message: String,
        details: Map<String, String> = emptyMap(),
    ) {
        // The normal MDC is ThreadLocal-based, so it is not a reliable async context carrier by itself.
        // We put it only around this single log call so the console pattern always shows requestId.
        val previous = MDC.get(RequestContextKeys.MDC_REQUEST_ID)
        MDC.put(RequestContextKeys.MDC_REQUEST_ID, requestId)
        try {
            log.info(
                "layer={} time={} thread={} message={} details={}",
                layer,
                now(),
                threadName(),
                message,
                details,
            )
        } finally {
            if (previous == null) {
                MDC.remove(RequestContextKeys.MDC_REQUEST_ID)
            } else {
                MDC.put(RequestContextKeys.MDC_REQUEST_ID, previous)
            }
        }
    }
}


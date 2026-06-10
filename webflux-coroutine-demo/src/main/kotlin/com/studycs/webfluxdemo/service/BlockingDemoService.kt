package com.studycs.webfluxdemo.service

import com.studycs.webfluxdemo.model.DemoResponse
import com.studycs.webfluxdemo.support.DemoLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import kotlin.system.measureTimeMillis

@Service
class BlockingDemoService {
    suspend fun blockingSleep(requestId: String, ms: Long): DemoResponse {
        val beforeThread = DemoLog.threadName()
        val elapsedMs = measureTimeMillis {
            DemoLog.mark(
                requestId = requestId,
                layer = "blocking-sleep",
                message = "BAD: Thread.sleep starts on the request handling thread",
                details = mapOf("sleepMs" to ms.toString()),
            )
            // This is intentionally wrong in WebFlux request handling.
            // If beforeThread is reactor-http-nio-*, this blocks a Netty event-loop thread.
            Thread.sleep(ms)
            DemoLog.mark(
                requestId = requestId,
                layer = "blocking-sleep",
                message = "Thread.sleep finished; the event-loop could not process other work during this period",
                details = mapOf("sleepMs" to ms.toString()),
            )
        }

        return DemoResponse(
            requestId = requestId,
            endpoint = "/demo/blocking/sleep",
            message = "Thread.sleep blocked the current thread",
            thread = DemoLog.threadName(),
            elapsedMs = elapsedMs,
            details = mapOf(
                "beforeThread" to beforeThread,
                "afterThread" to DemoLog.threadName(),
                "warning" to "Do not run blocking calls on reactor-http-nio event-loop threads.",
            ),
        )
    }

    suspend fun nonBlockingDelay(requestId: String, ms: Long): DemoResponse {
        val beforeThread = DemoLog.threadName()
        val elapsedMs = measureTimeMillis {
            DemoLog.mark(
                requestId = requestId,
                layer = "non-blocking-delay",
                message = "GOOD: delay schedules a timer and suspends the coroutine continuation",
                details = mapOf("delayMs" to ms.toString()),
            )
            kotlinx.coroutines.delay(ms)
            DemoLog.mark(
                requestId = requestId,
                layer = "non-blocking-delay",
                message = "delay completed; the event-loop was free while waiting",
                details = mapOf("delayMs" to ms.toString()),
            )
        }

        return DemoResponse(
            requestId = requestId,
            endpoint = "/demo/non-blocking/delay",
            message = "delay waited without blocking the current thread",
            thread = DemoLog.threadName(),
            elapsedMs = elapsedMs,
            details = mapOf(
                "beforeThread" to beforeThread,
                "afterThread" to DemoLog.threadName(),
            ),
        )
    }

    suspend fun offloadBlocking(requestId: String, ms: Long, mode: String): DemoResponse {
        val beforeThread = DemoLog.threadName()
        var blockingThread = ""
        val normalizedMode = mode.lowercase()
        val elapsedMs = measureTimeMillis {
            blockingThread = if (normalizedMode == "io") {
                offloadWithDispatchersIo(requestId, ms)
            } else {
                offloadWithBoundedElastic(requestId, ms)
            }
        }

        return DemoResponse(
            requestId = requestId,
            endpoint = "/demo/blocking/offload",
            message = "blocking work was moved away from the Netty event-loop",
            thread = DemoLog.threadName(),
            elapsedMs = elapsedMs,
            details = mapOf(
                "mode" to if (normalizedMode == "io") "Dispatchers.IO" else "Schedulers.boundedElastic",
                "beforeThread" to beforeThread,
                "blockingThread" to blockingThread,
                "afterAwaitThread" to DemoLog.threadName(),
            ),
        )
    }

    private suspend fun offloadWithBoundedElastic(requestId: String, ms: Long): String {
        return Mono.fromCallable {
            DemoLog.mark(
                requestId = requestId,
                layer = "boundedElastic",
                message = "blocking call is running on Reactor boundedElastic",
                details = mapOf("sleepMs" to ms.toString()),
            )
            Thread.sleep(ms)
            DemoLog.mark(
                requestId = requestId,
                layer = "boundedElastic",
                message = "blocking call finished on boundedElastic",
            )
            DemoLog.threadName()
        }
            .subscribeOn(Schedulers.boundedElastic())
            .awaitSingle()
    }

    private suspend fun offloadWithDispatchersIo(requestId: String, ms: Long): String {
        return withContext(Dispatchers.IO) {
            DemoLog.mark(
                requestId = requestId,
                layer = "dispatchers-io",
                message = "blocking call is running on Kotlin Dispatchers.IO",
                details = mapOf("sleepMs" to ms.toString()),
            )
            Thread.sleep(ms)
            DemoLog.mark(
                requestId = requestId,
                layer = "dispatchers-io",
                message = "blocking call finished on Dispatchers.IO",
            )
            DemoLog.threadName()
        }
    }
}


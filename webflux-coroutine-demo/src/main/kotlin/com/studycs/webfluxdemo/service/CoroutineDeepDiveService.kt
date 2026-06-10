package com.studycs.webfluxdemo.service

import com.studycs.webfluxdemo.model.ChildTaskResult
import com.studycs.webfluxdemo.model.ScopeOutcome
import com.studycs.webfluxdemo.model.StructuredConcurrencyResponse
import com.studycs.webfluxdemo.support.DemoLog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.supervisorScope
import org.springframework.stereotype.Service
import java.util.Collections

@Service
class CoroutineDeepDiveService {
    suspend fun structuredConcurrency(requestId: String): StructuredConcurrencyResponse {
        return StructuredConcurrencyResponse(
            requestId = requestId,
            endpoint = "/demo/coroutine/structured",
            coroutineScope = runCoroutineScopeExperiment(requestId),
            supervisorScope = runSupervisorScopeExperiment(requestId),
        )
    }

    private suspend fun runCoroutineScopeExperiment(requestId: String): ScopeOutcome {
        val events = Collections.synchronizedList(mutableListOf<ChildTaskResult>())
        return try {
            coroutineScope {
                awaitAll(
                    async { childWork(requestId, events, "scope-fast", 30, fail = false) },
                    async { childWork(requestId, events, "scope-failing", 10, fail = true) },
                    async { childWork(requestId, events, "scope-slow", 80, fail = false) },
                )
            }
            ScopeOutcome(
                status = "completed",
                events = events.toList(),
                explanation = "All children completed. This is not expected in this demo.",
            )
        } catch (ex: IllegalStateException) {
            ScopeOutcome(
                status = "cancelled-by-child-failure",
                events = events.toList(),
                explanation = "coroutineScope cancels sibling children when one child fails.",
            )
        }
    }

    private suspend fun runSupervisorScopeExperiment(requestId: String): ScopeOutcome {
        val events = Collections.synchronizedList(mutableListOf<ChildTaskResult>())
        supervisorScope {
            val jobs = listOf(
                async { childWork(requestId, events, "supervisor-fast", 30, fail = false) },
                async { childWork(requestId, events, "supervisor-failing", 10, fail = true) },
                async { childWork(requestId, events, "supervisor-slow", 80, fail = false) },
            )

            jobs.forEach { job ->
                runCatching { job.await() }
            }
        }

        return ScopeOutcome(
            status = "completed-with-isolated-failure",
            events = events.toList(),
            explanation = "supervisorScope isolates child failure so sibling children can complete.",
        )
    }

    private suspend fun childWork(
        requestId: String,
        events: MutableList<ChildTaskResult>,
        name: String,
        delayMs: Long,
        fail: Boolean,
    ): String {
        val startedAt = System.nanoTime()
        return try {
            delay(delayMs)
            if (fail) {
                throw IllegalStateException("planned failure from $name")
            }
            record(requestId, events, name, "success", startedAt, "completed after delay")
            name
        } catch (ex: CancellationException) {
            record(requestId, events, name, "cancelled", startedAt, "cancelled by parent scope")
            throw ex
        } catch (ex: IllegalStateException) {
            record(requestId, events, name, "failed", startedAt, ex.message ?: "failed")
            throw ex
        }
    }

    private fun record(
        requestId: String,
        events: MutableList<ChildTaskResult>,
        name: String,
        status: String,
        startedAt: Long,
        message: String,
    ) {
        val elapsedMs = (System.nanoTime() - startedAt) / 1_000_000
        val event = ChildTaskResult(
            name = name,
            status = status,
            thread = DemoLog.threadName(),
            elapsedMs = elapsedMs,
            message = message,
        )
        events += event
        DemoLog.mark(
            requestId = requestId,
            layer = "structured-concurrency",
            message = "$name $status",
            details = mapOf("elapsedMs" to elapsedMs.toString(), "message" to message),
        )
    }
}


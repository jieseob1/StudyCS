package com.studycs.webfluxdemo.service

import com.studycs.webfluxdemo.model.ContextResponse
import com.studycs.webfluxdemo.support.DemoLog
import com.studycs.webfluxdemo.support.RequestContextKeys
import com.studycs.webfluxdemo.support.currentReactorRequestId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import org.slf4j.MDC
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture

@Service
class ContextDemoService {
    fun reactorContext(requestId: String): Mono<ContextResponse> {
        return Mono.deferContextual { contextView ->
            val observed = contextView
                .getOrDefault(RequestContextKeys.REACTOR_CONTEXT_REQUEST_ID, "missing")
                .toString()
            DemoLog.mark(
                requestId = requestId,
                layer = "context-reactor",
                message = "read requestId directly from Reactor Context",
                details = mapOf("observed" to observed),
            )
            Mono.just(
                ContextResponse(
                    requestId = requestId,
                    observedRequestId = observed,
                    endpoint = "/demo/context/reactor",
                    thread = DemoLog.threadName(),
                    explanation = "Mono.deferContextual reads the value written by WebFilter.contextWrite.",
                ),
            )
        }
    }

    suspend fun coroutineContext(requestId: String): ContextResponse {
        val observed = currentReactorRequestId()
        DemoLog.mark(
            requestId = requestId,
            layer = "context-coroutine",
            message = "read Reactor Context through coroutine context bridge",
            details = mapOf("observed" to observed),
        )
        return ContextResponse(
            requestId = requestId,
            observedRequestId = observed,
            endpoint = "/demo/context/coroutine",
            thread = DemoLog.threadName(),
            explanation = "kotlinx-coroutines-reactor exposes ReactorContext inside suspend handlers.",
        )
    }

    suspend fun brokenContext(requestId: String): ContextResponse {
        val before = currentReactorRequestId()
        val observedInsideFuture = CompletableFuture.supplyAsync {
            // CompletableFuture.supplyAsync uses an unmanaged executor boundary here.
            // Reactor Context and coroutine context are not automatically installed in that thread.
            val observedFromMdc = MDC.get(RequestContextKeys.MDC_REQUEST_ID) ?: "missing"
            DemoLog.mark(
                requestId = observedFromMdc,
                layer = "context-broken",
                message = "plain CompletableFuture does not receive Reactor/Coroutine context automatically",
                details = mapOf("beforeCoroutineContext" to before),
            )
            observedFromMdc
        }.await()

        return ContextResponse(
            requestId = requestId,
            observedRequestId = observedInsideFuture,
            endpoint = "/demo/context/broken",
            thread = DemoLog.threadName(),
            explanation = "Leaving Reactor/coroutine-managed execution with plain CompletableFuture loses implicit context.",
            details = mapOf("beforeCompletableFuture" to before),
        )
    }

    suspend fun fixedContext(requestId: String): ContextResponse {
        val observedInsideDispatcher = withContext(Dispatchers.IO) {
            val observed = currentReactorRequestId()
            DemoLog.mark(
                requestId = requestId,
                layer = "context-fixed",
                message = "withContext switches dispatcher but keeps the parent CoroutineContext",
                details = mapOf("observed" to observed),
            )
            observed
        }

        return ContextResponse(
            requestId = requestId,
            observedRequestId = observedInsideDispatcher,
            endpoint = "/demo/context/fixed",
            thread = DemoLog.threadName(),
            explanation = "withContext preserves coroutine context elements, including ReactorContext.",
        )
    }
}


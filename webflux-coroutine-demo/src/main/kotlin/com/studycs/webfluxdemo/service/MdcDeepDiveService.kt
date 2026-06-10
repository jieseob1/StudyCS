package com.studycs.webfluxdemo.service

import com.studycs.webfluxdemo.model.ContextResponse
import com.studycs.webfluxdemo.support.DemoLog
import com.studycs.webfluxdemo.support.RequestContextKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.slf4j.MDC
import org.springframework.stereotype.Service

@Service
class MdcDeepDiveService {
    suspend fun broken(requestId: String): ContextResponse {
        return withRequestIdInMdc(requestId) {
            val observed = withContext(Dispatchers.IO) {
                // MDC is ThreadLocal. A dispatcher switch does not carry it unless we install MDCContext.
                MDC.get(RequestContextKeys.MDC_REQUEST_ID) ?: "missing"
            }

            DemoLog.mark(
                requestId = requestId,
                layer = "mdc-broken",
                message = "MDC did not automatically cross dispatcher boundary",
                details = mapOf("observed" to observed),
            )

            ContextResponse(
                requestId = requestId,
                observedRequestId = observed,
                endpoint = "/demo/context/mdc/broken",
                thread = DemoLog.threadName(),
                explanation = "MDC is ThreadLocal and is not a coroutine context element by default.",
            )
        }
    }

    suspend fun fixed(requestId: String): ContextResponse {
        return withRequestIdInMdc(requestId) {
            val observed = withContext(Dispatchers.IO + MDCContext()) {
                // MDCContext captures the current MDC map and restores it around coroutine resumption.
                MDC.get(RequestContextKeys.MDC_REQUEST_ID) ?: "missing"
            }

            DemoLog.mark(
                requestId = requestId,
                layer = "mdc-fixed",
                message = "MDCContext carried requestId across dispatcher boundary",
                details = mapOf("observed" to observed),
            )

            ContextResponse(
                requestId = requestId,
                observedRequestId = observed,
                endpoint = "/demo/context/mdc/fixed",
                thread = DemoLog.threadName(),
                explanation = "MDCContext explicitly bridges ThreadLocal MDC into CoroutineContext.",
            )
        }
    }

    private suspend fun withRequestIdInMdc(
        requestId: String,
        block: suspend () -> ContextResponse,
    ): ContextResponse {
        val previous = MDC.get(RequestContextKeys.MDC_REQUEST_ID)
        MDC.put(RequestContextKeys.MDC_REQUEST_ID, requestId)
        return try {
            block()
        } finally {
            if (previous == null) {
                MDC.remove(RequestContextKeys.MDC_REQUEST_ID)
            } else {
                MDC.put(RequestContextKeys.MDC_REQUEST_ID, previous)
            }
        }
    }
}


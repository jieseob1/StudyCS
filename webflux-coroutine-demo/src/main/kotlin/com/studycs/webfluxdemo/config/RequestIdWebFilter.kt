package com.studycs.webfluxdemo.config

import com.studycs.webfluxdemo.support.DemoLog
import com.studycs.webfluxdemo.support.RequestContextKeys
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class RequestIdWebFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val requestId = exchange.request.headers.getFirst(RequestContextKeys.HEADER_REQUEST_ID)
            ?: UUID.randomUUID().toString()

        exchange.attributes[RequestContextKeys.ATTRIBUTE_REQUEST_ID] = requestId
        exchange.response.headers.add(RequestContextKeys.HEADER_REQUEST_ID, requestId)

        DemoLog.mark(
            requestId = requestId,
            layer = "web-filter",
            message = "request id created and written to exchange attribute, response header, and Reactor Context",
            details = mapOf("path" to exchange.request.path.value()),
        )

        return chain.filter(exchange)
            // This is the async-safe request-scoped data carrier in Reactor pipelines.
            // suspend handlers can read it through kotlinx-coroutines-reactor's ReactorContext.
            .contextWrite { context ->
                context.put(RequestContextKeys.REACTOR_CONTEXT_REQUEST_ID, requestId)
            }
    }
}


package com.studycs.webfluxdemo.support

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.reactor.ReactorContext
import org.springframework.web.server.ServerWebExchange

object RequestContextKeys {
    const val HEADER_REQUEST_ID = "X-Request-Id"
    const val ATTRIBUTE_REQUEST_ID = "demo.requestId"
    const val REACTOR_CONTEXT_REQUEST_ID = "requestId"
    const val MDC_REQUEST_ID = "requestId"
}

fun ServerWebExchange.requestId(): String =
    getAttribute<String>(RequestContextKeys.ATTRIBUTE_REQUEST_ID)
        ?: request.headers.getFirst(RequestContextKeys.HEADER_REQUEST_ID)
        ?: "missing"

suspend fun currentReactorRequestId(): String {
    // kotlinx-coroutines-reactor installs ReactorContext into coroutine context when a suspend
    // WebFlux handler is invoked. That is why this can read the value written by WebFilter.contextWrite.
    val reactorContext = currentCoroutineContext()[ReactorContext]?.context
    return reactorContext
        ?.getOrDefault(RequestContextKeys.REACTOR_CONTEXT_REQUEST_ID, "missing")
        ?.toString()
        ?: "missing"
}

fun ServerWebExchange.baseUrl(): String {
    val uri = request.uri
    val scheme = uri.scheme ?: "http"
    val hostHeader = request.headers.host
    val host = hostHeader?.hostString ?: uri.host ?: "localhost"
    val resolvedPort = hostHeader?.port?.takeIf { it != -1 }
        ?: uri.port.takeIf { it != -1 }
        ?: request.localAddress?.port
        ?: -1
    val port = if (resolvedPort == -1) "" else ":$resolvedPort"
    return "$scheme://$host$port"
}

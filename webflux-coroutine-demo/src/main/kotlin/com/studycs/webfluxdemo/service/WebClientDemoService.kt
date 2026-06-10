package com.studycs.webfluxdemo.service

import com.studycs.webfluxdemo.model.MockApiResponse
import com.studycs.webfluxdemo.model.WebClientDemoResponse
import com.studycs.webfluxdemo.support.DemoLog
import com.studycs.webfluxdemo.support.RequestContextKeys
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import kotlin.system.measureTimeMillis

@Service
class WebClientDemoService(
    private val webClientBuilder: WebClient.Builder,
    private val applicationContext: ReactiveWebServerApplicationContext,
) {
    suspend fun sequential(baseUrl: String, requestId: String, ids: List<Int>, delayMs: Long): WebClientDemoResponse {
        val results = mutableListOf<MockApiResponse>()
        val elapsedMs = measureTimeMillis {
            for (id in ids) {
                DemoLog.mark(
                    requestId = requestId,
                    layer = "webclient-sequential",
                    message = "calling mock api one by one",
                    details = mapOf("id" to id.toString()),
                )
                results += callSlowApi(baseUrl, requestId, id, delayMs)
            }
        }

        return WebClientDemoResponse(
            requestId = requestId,
            endpoint = "/demo/webclient/sequential",
            elapsedMs = elapsedMs,
            thread = DemoLog.threadName(),
            results = results,
            details = mapOf("expectedShape" to "roughly ids.size * delayMs"),
        )
    }

    suspend fun parallel(baseUrl: String, requestId: String, ids: List<Int>, delayMs: Long): WebClientDemoResponse {
        lateinit var results: List<MockApiResponse>
        val elapsedMs = measureTimeMillis {
            results = coroutineScope {
                ids.map { id ->
                    async {
                        DemoLog.mark(
                            requestId = requestId,
                            layer = "webclient-parallel",
                            message = "starting concurrent WebClient call",
                            details = mapOf("id" to id.toString()),
                        )
                        callSlowApi(baseUrl, requestId, id, delayMs)
                    }
                }.awaitAll()
            }
        }

        return WebClientDemoResponse(
            requestId = requestId,
            endpoint = "/demo/webclient/parallel",
            elapsedMs = elapsedMs,
            thread = DemoLog.threadName(),
            results = results,
            details = mapOf("expectedShape" to "roughly max(delayMs) when calls run together"),
        )
    }

    private suspend fun callSlowApi(baseUrl: String, requestId: String, id: Int, delayMs: Long): MockApiResponse {
        val resolvedBaseUrl = resolveBaseUrl(baseUrl)
        return webClientBuilder.clone()
            .baseUrl(resolvedBaseUrl)
            .build()
            .get()
            .uri { builder: UriBuilder ->
                builder
                    .path("/demo/mock/slow-api/{id}")
                    .queryParam("delayMs", delayMs)
                    .build(id)
            }
            .header(RequestContextKeys.HEADER_REQUEST_ID, requestId)
            .retrieve()
            .bodyToMono(MockApiResponse::class.java)
            .doOnSubscribe {
                DemoLog.mark(
                    requestId = requestId,
                    layer = "webclient",
                    message = "WebClient subscribed; Netty client I/O is non-blocking",
                    details = mapOf("id" to id.toString(), "baseUrl" to resolvedBaseUrl),
                )
            }
            .awaitSingle()
    }

    private fun resolveBaseUrl(baseUrl: String): String {
        val authority = baseUrl.substringAfter("://", baseUrl)
        val alreadyHasPort = ":" in authority
        val actualPort = applicationContext.webServer.port
        return if (!alreadyHasPort && actualPort > 0) {
            "$baseUrl:$actualPort"
        } else {
            baseUrl
        }
    }
}

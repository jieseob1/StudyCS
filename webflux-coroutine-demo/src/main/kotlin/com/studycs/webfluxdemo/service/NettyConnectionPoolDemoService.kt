package com.studycs.webfluxdemo.service

import com.studycs.webfluxdemo.model.ConnectionPoolDemoResponse
import com.studycs.webfluxdemo.model.MockApiResponse
import com.studycs.webfluxdemo.support.DemoLog
import com.studycs.webfluxdemo.support.RequestContextKeys
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import kotlin.system.measureTimeMillis

@Service
class NettyConnectionPoolDemoService(
    private val applicationContext: ReactiveWebServerApplicationContext,
) {
    suspend fun runPoolExperiment(
        baseUrl: String,
        requestId: String,
        ids: List<Int>,
        delayMs: Long,
        maxConnections: Int,
    ): ConnectionPoolDemoResponse {
        val safeConnections = maxConnections.coerceIn(1, 32)
        val provider = ConnectionProvider.builder("demo-pool-$safeConnections-${System.nanoTime()}")
            .maxConnections(safeConnections)
            .pendingAcquireMaxCount(1_000)
            .build()
        val resolvedBaseUrl = resolveBaseUrl(baseUrl)
        val webClient = WebClient.builder()
            .baseUrl(resolvedBaseUrl)
            .clientConnector(ReactorClientHttpConnector(HttpClient.create(provider)))
            .build()

        lateinit var results: List<MockApiResponse>
        val elapsedMs = try {
            measureTimeMillis {
                results = coroutineScope {
                    ids.map { id ->
                        async {
                            callSlowApi(webClient, requestId, id, delayMs)
                        }
                    }.awaitAll()
                }
            }
        } finally {
            provider.disposeLater().awaitSingleOrNull()
        }

        return ConnectionPoolDemoResponse(
            requestId = requestId,
            endpoint = "/demo/netty/connection-pool",
            maxConnections = safeConnections,
            elapsedMs = elapsedMs,
            results = results,
            details = mapOf(
                "ids" to ids.joinToString(","),
                "delayMs" to delayMs.toString(),
                "baseUrl" to resolvedBaseUrl,
                "whatToCompare" to "connections=1 should serialize more than connections=4 for same ids/delayMs.",
            ),
        )
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

    private suspend fun callSlowApi(
        webClient: WebClient,
        requestId: String,
        id: Int,
        delayMs: Long,
    ): MockApiResponse {
        return webClient.get()
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
                    layer = "netty-pool",
                    message = "subscribed WebClient call using constrained ConnectionProvider",
                    details = mapOf("id" to id.toString()),
                )
            }
            .awaitSingle()
    }
}

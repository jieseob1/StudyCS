package com.studycs.webfluxdemo

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "15s")
class DeepDiveEndpointTest(
    @Autowired private val webTestClient: WebTestClient,
) {
    @Test
    fun `scheduler endpoints show subscribeOn and publishOn stages`() {
        webTestClient.get()
            .uri("/demo/reactor/scheduler/subscribe-on")
            .header("X-Request-Id", "test-scheduler")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.requestId").isEqualTo("test-scheduler")
            .jsonPath("$.mode").isEqualTo("subscribeOn")
            .jsonPath("$.steps[0].name").isEqualTo("source")

        webTestClient.get()
            .uri("/demo/reactor/scheduler/publish-on")
            .header("X-Request-Id", "test-scheduler")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.requestId").isEqualTo("test-scheduler")
            .jsonPath("$.mode").isEqualTo("publishOn")
            .jsonPath("$.steps[0].name").isEqualTo("source")
            .jsonPath("$.steps[2].name").isEqualTo("after-publishOn")
    }

    @Test
    fun `fusion endpoint exposes fuseable and hidden operator difference`() {
        webTestClient.get()
            .uri("/demo/reactor/fusion")
            .header("X-Request-Id", "test-fusion")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.requestId").isEqualTo("test-fusion")
            .jsonPath("$.fusedIsFuseable").isEqualTo(true)
            .jsonPath("$.hiddenIsFuseable").isEqualTo(false)
            .jsonPath("$.fusedResult.length()").isEqualTo(5)
    }

    @Test
    fun `netty connection pool endpoint is callable with constrained pool`() {
        webTestClient.get()
            .uri("/demo/netty/connection-pool?connections=1&ids=1,2&delayMs=20")
            .header("X-Request-Id", "test-pool")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.requestId").isEqualTo("test-pool")
            .jsonPath("$.maxConnections").isEqualTo(1)
            .jsonPath("$.results.length()").isEqualTo(2)
    }

    @Test
    fun `jdbc and r2dbc endpoints compare blocking and non blocking database access`() {
        listOf(
            "/demo/db/jdbc/bad?ms=5",
            "/demo/db/jdbc/offload?ms=5",
            "/demo/db/r2dbc?ms=5",
        ).forEach { path ->
            webTestClient.get()
                .uri(path)
                .header("X-Request-Id", "test-db")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.requestId").isEqualTo("test-db")
                .jsonPath("$.rowCount").isEqualTo(3)
        }
    }

    @Test
    fun `structured concurrency endpoint shows child failure cancellation and supervisor isolation`() {
        webTestClient.get()
            .uri("/demo/coroutine/structured")
            .header("X-Request-Id", "test-structured")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.requestId").isEqualTo("test-structured")
            .jsonPath("$.coroutineScope.status").isEqualTo("cancelled-by-child-failure")
            .jsonPath("$.supervisorScope.status").isEqualTo("completed-with-isolated-failure")
    }

    @Test
    fun `mdc endpoints show thread local context loss and explicit propagation`() {
        webTestClient.get()
            .uri("/demo/context/mdc/broken")
            .header("X-Request-Id", "test-mdc")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.requestId").isEqualTo("test-mdc")
            .jsonPath("$.observedRequestId").isEqualTo("missing")

        webTestClient.get()
            .uri("/demo/context/mdc/fixed")
            .header("X-Request-Id", "test-mdc")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.requestId").isEqualTo("test-mdc")
            .jsonPath("$.observedRequestId").isEqualTo("test-mdc")
    }
}


package com.studycs.webfluxdemo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "10s")
class DemoEndpointTest(
    @Autowired private val webTestClient: WebTestClient,
) {
    @Test
    fun `hello endpoint returns request id and controller-service-repository flow`() {
        webTestClient.get()
            .uri("/demo/hello")
            .header("X-Request-Id", "test-hello")
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals("X-Request-Id", "test-hello")
            .expectBody()
            .jsonPath("$.requestId").isEqualTo("test-hello")
            .jsonPath("$.endpoint").isEqualTo("/demo/hello")
            .jsonPath("$.details.repository").isEqualTo("visited")
    }

    @Test
    fun `blocking non blocking and offload endpoints are callable`() {
        listOf(
            "/demo/blocking/sleep?ms=10",
            "/demo/non-blocking/delay?ms=10",
            "/demo/blocking/offload?ms=10",
            "/demo/blocking/offload?ms=10&mode=io",
        ).forEach { path ->
            webTestClient.get()
                .uri(path)
                .header("X-Request-Id", "test-blocking")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.requestId").isEqualTo("test-blocking")
        }
    }

    @Test
    fun `reactor and coroutine item endpoints expose the same learning data`() {
        webTestClient.get()
            .uri("/demo/reactor/item/1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.style").isEqualTo("reactor-mono")

        val reactorItems = webTestClient.get()
            .uri("/demo/reactor/items")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(ItemResponseForTest::class.java)
            .returnResult()
            .responseBody

        val coroutineItems = webTestClient.get()
            .uri("/demo/coroutine/items")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(ItemResponseForTest::class.java)
            .returnResult()
            .responseBody

        assertThat(reactorItems).hasSize(5)
        assertThat(coroutineItems).hasSize(5)
        assertThat(coroutineItems).allMatch { it.style == "coroutine-flow" }
    }

    @Test
    fun `webclient sequential and parallel endpoints call the mock slow api`() {
        listOf(
            "/demo/webclient/sequential?ids=1,2&delayMs=10",
            "/demo/webclient/parallel?ids=1,2&delayMs=10",
        ).forEach { path ->
            webTestClient.get()
                .uri(path)
                .header("X-Request-Id", "test-webclient")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.requestId").isEqualTo("test-webclient")
                .jsonPath("$.results.length()").isEqualTo(2)
        }
    }

    @Test
    fun `reactor context survives coroutine bridge but plain completable future loses it`() {
        webTestClient.get()
            .uri("/demo/context/coroutine")
            .header("X-Request-Id", "test-context")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.requestId").isEqualTo("test-context")
            .jsonPath("$.observedRequestId").isEqualTo("test-context")

        webTestClient.get()
            .uri("/demo/context/broken")
            .header("X-Request-Id", "test-context")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.requestId").isEqualTo("test-context")
            .jsonPath("$.observedRequestId").isEqualTo("missing")

        webTestClient.get()
            .uri("/demo/context/fixed")
            .header("X-Request-Id", "test-context")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.requestId").isEqualTo("test-context")
            .jsonPath("$.observedRequestId").isEqualTo("test-context")
    }
}

data class ItemResponseForTest(
    val id: Int = 0,
    val name: String = "",
    val style: String = "",
)


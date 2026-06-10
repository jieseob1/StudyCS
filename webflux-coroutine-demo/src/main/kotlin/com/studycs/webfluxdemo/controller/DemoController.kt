package com.studycs.webfluxdemo.controller

import com.studycs.webfluxdemo.model.DemoResponse
import com.studycs.webfluxdemo.model.ItemResponse
import com.studycs.webfluxdemo.service.BasicFlowService
import com.studycs.webfluxdemo.service.BlockingDemoService
import com.studycs.webfluxdemo.service.ItemDemoService
import com.studycs.webfluxdemo.support.DemoLog
import com.studycs.webfluxdemo.support.requestId
import kotlinx.coroutines.flow.Flow
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class DemoController(
    private val basicFlowService: BasicFlowService,
    private val blockingDemoService: BlockingDemoService,
    private val itemDemoService: ItemDemoService,
) {
    @GetMapping("/demo/hello")
    suspend fun hello(exchange: ServerWebExchange): DemoResponse {
        val requestId = exchange.requestId()
        DemoLog.mark(
            requestId = requestId,
            layer = "controller",
            message = "request entered suspend controller",
        )
        return basicFlowService.hello(requestId)
    }

    @GetMapping("/demo/blocking/sleep")
    suspend fun blockingSleep(
        exchange: ServerWebExchange,
        @RequestParam(defaultValue = "1000") ms: Long,
    ): DemoResponse =
        blockingDemoService.blockingSleep(exchange.requestId(), ms)

    @GetMapping("/demo/non-blocking/delay")
    suspend fun nonBlockingDelay(
        exchange: ServerWebExchange,
        @RequestParam(defaultValue = "1000") ms: Long,
    ): DemoResponse =
        blockingDemoService.nonBlockingDelay(exchange.requestId(), ms)

    @GetMapping("/demo/blocking/offload")
    suspend fun blockingOffload(
        exchange: ServerWebExchange,
        @RequestParam(defaultValue = "1000") ms: Long,
        @RequestParam(defaultValue = "boundedElastic") mode: String,
    ): DemoResponse =
        blockingDemoService.offloadBlocking(exchange.requestId(), ms, mode)

    @GetMapping("/demo/reactor/item/{id}")
    fun reactorItem(exchange: ServerWebExchange, @PathVariable id: Int): Mono<ItemResponse> =
        itemDemoService.getItemReactor(id, exchange.requestId())

    @GetMapping("/demo/reactor/items")
    fun reactorItems(exchange: ServerWebExchange): Flux<ItemResponse> =
        itemDemoService.getItemsReactor(exchange.requestId())

    @GetMapping("/demo/coroutine/item/{id}")
    suspend fun coroutineItem(exchange: ServerWebExchange, @PathVariable id: Int): ItemResponse =
        itemDemoService.getItemCoroutine(id, exchange.requestId())

    @GetMapping("/demo/coroutine/items")
    fun coroutineItems(exchange: ServerWebExchange): Flow<ItemResponse> =
        itemDemoService.getItemsCoroutine(exchange.requestId())

    @GetMapping("/demo/flux/flatmap", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun flatMapConcurrency(
        exchange: ServerWebExchange,
        @RequestParam(defaultValue = "20") count: Int,
        @RequestParam(defaultValue = "4") concurrency: Int,
    ): Flux<ItemResponse> =
        itemDemoService.flatMapConcurrency(
            requestId = exchange.requestId(),
            count = count.coerceIn(1, 200),
            concurrency = concurrency.coerceIn(1, 64),
        )
}


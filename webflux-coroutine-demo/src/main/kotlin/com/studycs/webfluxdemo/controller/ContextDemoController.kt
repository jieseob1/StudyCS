package com.studycs.webfluxdemo.controller

import com.studycs.webfluxdemo.model.ContextResponse
import com.studycs.webfluxdemo.service.ContextDemoService
import com.studycs.webfluxdemo.support.requestId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
class ContextDemoController(
    private val contextDemoService: ContextDemoService,
) {
    @GetMapping("/demo/context/reactor")
    fun reactorContext(exchange: ServerWebExchange): Mono<ContextResponse> =
        contextDemoService.reactorContext(exchange.requestId())

    @GetMapping("/demo/context/coroutine")
    suspend fun coroutineContext(exchange: ServerWebExchange): ContextResponse =
        contextDemoService.coroutineContext(exchange.requestId())

    @GetMapping("/demo/context/broken")
    suspend fun brokenContext(exchange: ServerWebExchange): ContextResponse =
        contextDemoService.brokenContext(exchange.requestId())

    @GetMapping("/demo/context/fixed")
    suspend fun fixedContext(exchange: ServerWebExchange): ContextResponse =
        contextDemoService.fixedContext(exchange.requestId())
}


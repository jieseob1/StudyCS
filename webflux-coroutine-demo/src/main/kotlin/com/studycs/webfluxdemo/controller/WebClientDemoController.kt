package com.studycs.webfluxdemo.controller

import com.studycs.webfluxdemo.model.WebClientDemoResponse
import com.studycs.webfluxdemo.service.WebClientDemoService
import com.studycs.webfluxdemo.support.baseUrl
import com.studycs.webfluxdemo.support.requestId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
class WebClientDemoController(
    private val webClientDemoService: WebClientDemoService,
) {
    @GetMapping("/demo/webclient/sequential")
    suspend fun sequential(
        exchange: ServerWebExchange,
        @RequestParam(defaultValue = "1,2,3,4,5") ids: String,
        @RequestParam(defaultValue = "300") delayMs: Long,
    ): WebClientDemoResponse =
        webClientDemoService.sequential(
            baseUrl = exchange.baseUrl(),
            requestId = exchange.requestId(),
            ids = parseIds(ids),
            delayMs = delayMs,
        )

    @GetMapping("/demo/webclient/parallel")
    suspend fun parallel(
        exchange: ServerWebExchange,
        @RequestParam(defaultValue = "1,2,3,4,5") ids: String,
        @RequestParam(defaultValue = "300") delayMs: Long,
    ): WebClientDemoResponse =
        webClientDemoService.parallel(
            baseUrl = exchange.baseUrl(),
            requestId = exchange.requestId(),
            ids = parseIds(ids),
            delayMs = delayMs,
        )

    private fun parseIds(ids: String): List<Int> =
        ids.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .ifEmpty { listOf(1, 2, 3, 4, 5) }
}


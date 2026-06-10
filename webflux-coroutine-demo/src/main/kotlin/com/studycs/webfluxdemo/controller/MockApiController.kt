package com.studycs.webfluxdemo.controller

import com.studycs.webfluxdemo.model.MockApiResponse
import com.studycs.webfluxdemo.support.DemoLog
import com.studycs.webfluxdemo.support.requestId
import kotlinx.coroutines.delay
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
class MockApiController {
    @GetMapping("/demo/mock/slow-api/{id}")
    suspend fun slowApi(
        exchange: ServerWebExchange,
        @PathVariable id: Int,
        @RequestParam(defaultValue = "300") delayMs: Long,
    ): MockApiResponse {
        val requestId = exchange.requestId()
        DemoLog.mark(
            requestId = requestId,
            layer = "mock-api",
            message = "mock slow api entered; delay is non-blocking",
            details = mapOf("id" to id.toString(), "delayMs" to delayMs.toString()),
        )
        delay(delayMs)
        DemoLog.mark(
            requestId = requestId,
            layer = "mock-api",
            message = "mock slow api completed",
            details = mapOf("id" to id.toString()),
        )
        return MockApiResponse(
            id = id,
            delayMs = delayMs,
            requestId = requestId,
            thread = DemoLog.threadName(),
            timestamp = DemoLog.now(),
        )
    }
}


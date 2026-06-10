package com.studycs.webfluxdemo.controller

import com.studycs.webfluxdemo.model.ConnectionPoolDemoResponse
import com.studycs.webfluxdemo.model.ContextResponse
import com.studycs.webfluxdemo.model.DbDemoResponse
import com.studycs.webfluxdemo.model.FusionDemoResponse
import com.studycs.webfluxdemo.model.SchedulerDemoResponse
import com.studycs.webfluxdemo.model.StructuredConcurrencyResponse
import com.studycs.webfluxdemo.service.CoroutineDeepDiveService
import com.studycs.webfluxdemo.service.DatabaseDeepDiveService
import com.studycs.webfluxdemo.service.MdcDeepDiveService
import com.studycs.webfluxdemo.service.NettyConnectionPoolDemoService
import com.studycs.webfluxdemo.service.SchedulerDeepDiveService
import com.studycs.webfluxdemo.support.baseUrl
import com.studycs.webfluxdemo.support.requestId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
class DeepDiveController(
    private val schedulerDeepDiveService: SchedulerDeepDiveService,
    private val nettyConnectionPoolDemoService: NettyConnectionPoolDemoService,
    private val databaseDeepDiveService: DatabaseDeepDiveService,
    private val coroutineDeepDiveService: CoroutineDeepDiveService,
    private val mdcDeepDiveService: MdcDeepDiveService,
) {
    @GetMapping("/demo/reactor/scheduler/subscribe-on")
    suspend fun subscribeOn(exchange: ServerWebExchange): SchedulerDemoResponse =
        schedulerDeepDiveService.subscribeOn(exchange.requestId())

    @GetMapping("/demo/reactor/scheduler/publish-on")
    suspend fun publishOn(exchange: ServerWebExchange): SchedulerDemoResponse =
        schedulerDeepDiveService.publishOn(exchange.requestId())

    @GetMapping("/demo/reactor/fusion")
    suspend fun fusion(exchange: ServerWebExchange): FusionDemoResponse =
        schedulerDeepDiveService.fusion(exchange.requestId())

    @GetMapping("/demo/netty/connection-pool")
    suspend fun connectionPool(
        exchange: ServerWebExchange,
        @RequestParam(defaultValue = "1,2,3,4") ids: String,
        @RequestParam(defaultValue = "300") delayMs: Long,
        @RequestParam(defaultValue = "1") connections: Int,
    ): ConnectionPoolDemoResponse =
        nettyConnectionPoolDemoService.runPoolExperiment(
            baseUrl = exchange.baseUrl(),
            requestId = exchange.requestId(),
            ids = parseIds(ids),
            delayMs = delayMs,
            maxConnections = connections,
        )

    @GetMapping("/demo/db/jdbc/bad")
    suspend fun jdbcBad(
        exchange: ServerWebExchange,
        @RequestParam(defaultValue = "200") ms: Long,
    ): DbDemoResponse =
        databaseDeepDiveService.jdbcBad(exchange.requestId(), ms)

    @GetMapping("/demo/db/jdbc/offload")
    suspend fun jdbcOffload(
        exchange: ServerWebExchange,
        @RequestParam(defaultValue = "200") ms: Long,
    ): DbDemoResponse =
        databaseDeepDiveService.jdbcOffload(exchange.requestId(), ms)

    @GetMapping("/demo/db/r2dbc")
    suspend fun r2dbc(
        exchange: ServerWebExchange,
        @RequestParam(defaultValue = "200") ms: Long,
    ): DbDemoResponse =
        databaseDeepDiveService.r2dbc(exchange.requestId(), ms)

    @GetMapping("/demo/coroutine/structured")
    suspend fun structured(exchange: ServerWebExchange): StructuredConcurrencyResponse =
        coroutineDeepDiveService.structuredConcurrency(exchange.requestId())

    @GetMapping("/demo/context/mdc/broken")
    suspend fun mdcBroken(exchange: ServerWebExchange): ContextResponse =
        mdcDeepDiveService.broken(exchange.requestId())

    @GetMapping("/demo/context/mdc/fixed")
    suspend fun mdcFixed(exchange: ServerWebExchange): ContextResponse =
        mdcDeepDiveService.fixed(exchange.requestId())

    private fun parseIds(ids: String): List<Int> =
        ids.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .ifEmpty { listOf(1, 2, 3, 4) }
}


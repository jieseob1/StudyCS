package com.studycs.webfluxdemo.model

data class DemoResponse(
    val requestId: String,
    val endpoint: String,
    val message: String,
    val thread: String,
    val elapsedMs: Long,
    val details: Map<String, String> = emptyMap(),
)

data class ItemResponse(
    val id: Int,
    val name: String,
    val style: String,
    val thread: String,
)

data class MockApiResponse(
    val id: Int,
    val delayMs: Long,
    val requestId: String,
    val thread: String,
    val timestamp: String,
)

data class WebClientDemoResponse(
    val requestId: String,
    val endpoint: String,
    val elapsedMs: Long,
    val thread: String,
    val results: List<MockApiResponse>,
    val details: Map<String, String> = emptyMap(),
)

data class ContextResponse(
    val requestId: String,
    val observedRequestId: String,
    val endpoint: String,
    val thread: String,
    val explanation: String,
    val details: Map<String, String> = emptyMap(),
)

data class ThreadStep(
    val name: String,
    val thread: String,
    val elapsedMs: Long,
    val detail: String,
)

data class SchedulerDemoResponse(
    val requestId: String,
    val endpoint: String,
    val mode: String,
    val result: String,
    val steps: List<ThreadStep>,
    val explanation: String,
)

data class FusionDemoResponse(
    val requestId: String,
    val endpoint: String,
    val fusedIsFuseable: Boolean,
    val hiddenIsFuseable: Boolean,
    val fusedClass: String,
    val hiddenClass: String,
    val fusedResult: List<Int>,
    val hiddenResult: List<Int>,
    val explanation: String,
)

data class ConnectionPoolDemoResponse(
    val requestId: String,
    val endpoint: String,
    val maxConnections: Int,
    val elapsedMs: Long,
    val results: List<MockApiResponse>,
    val details: Map<String, String>,
)

data class ChildTaskResult(
    val name: String,
    val status: String,
    val thread: String,
    val elapsedMs: Long,
    val message: String,
)

data class ScopeOutcome(
    val status: String,
    val events: List<ChildTaskResult>,
    val explanation: String,
)

data class StructuredConcurrencyResponse(
    val requestId: String,
    val endpoint: String,
    val coroutineScope: ScopeOutcome,
    val supervisorScope: ScopeOutcome,
)

data class DbDemoResponse(
    val requestId: String,
    val endpoint: String,
    val accessStyle: String,
    val rowCount: Int,
    val elapsedMs: Long,
    val thread: String,
    val details: Map<String, String>,
)

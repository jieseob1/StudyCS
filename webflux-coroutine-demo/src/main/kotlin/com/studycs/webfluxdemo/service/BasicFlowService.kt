package com.studycs.webfluxdemo.service

import com.studycs.webfluxdemo.model.DemoResponse
import com.studycs.webfluxdemo.repository.DemoRepository
import com.studycs.webfluxdemo.support.DemoLog
import org.springframework.stereotype.Service
import kotlin.system.measureTimeMillis

@Service
class BasicFlowService(
    private val demoRepository: DemoRepository,
) {
    suspend fun hello(requestId: String): DemoResponse {
        var greeting = ""
        val elapsedMs = measureTimeMillis {
            DemoLog.mark(
                requestId = requestId,
                layer = "service",
                message = "controller called service; still normally on a reactor-http-nio event-loop",
            )
            greeting = demoRepository.loadGreeting(requestId)
        }

        DemoLog.mark(
            requestId = requestId,
            layer = "service",
            message = "service completed repository call",
        )

        return DemoResponse(
            requestId = requestId,
            endpoint = "/demo/hello",
            message = greeting,
            thread = DemoLog.threadName(),
            elapsedMs = elapsedMs,
            details = mapOf(
                "controller" to "visited",
                "service" to "visited",
                "repository" to "visited",
            ),
        )
    }
}


package com.studycs.webfluxdemo.repository

import com.studycs.webfluxdemo.model.ItemResponse
import com.studycs.webfluxdemo.support.DemoLog
import kotlinx.coroutines.delay
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Duration

@Repository
class DemoRepository {
    suspend fun loadGreeting(requestId: String): String {
        DemoLog.mark(
            requestId = requestId,
            layer = "repository",
            message = "before delay: coroutine suspends the continuation, not the Netty event-loop thread",
        )
        delay(50)
        DemoLog.mark(
            requestId = requestId,
            layer = "repository",
            message = "after delay: continuation resumed after timer completion",
        )
        return "hello from repository"
    }

    fun reactorItem(id: Int, requestId: String, style: String): Mono<ItemResponse> {
        // Mono is lazy. Nothing in this block runs until WebFlux subscribes to the returned Mono.
        return Mono.defer {
            DemoLog.mark(
                requestId = requestId,
                layer = "repository-reactor",
                message = "Mono.defer invoked by subscription",
                details = mapOf("id" to id.toString(), "style" to style),
            )
            Mono.delay(Duration.ofMillis(40))
                .map {
                    DemoLog.mark(
                        requestId = requestId,
                        layer = "repository-reactor",
                        message = "Mono.delay completed without occupying a worker thread during the wait",
                        details = mapOf("id" to id.toString()),
                    )
                    ItemResponse(
                        id = id,
                        name = "item-$id",
                        style = style,
                        thread = DemoLog.threadName(),
                    )
                }
        }
    }

    suspend fun coroutineItem(id: Int, requestId: String, style: String): ItemResponse {
        // suspend does not mean "run on a new thread." It means this function can pause and resume
        // without blocking the caller's thread while it waits for a suspending operation.
        DemoLog.mark(
            requestId = requestId,
            layer = "repository-coroutine",
            message = "before delay in suspend repository function",
            details = mapOf("id" to id.toString(), "style" to style),
        )
        delay(40)
        DemoLog.mark(
            requestId = requestId,
            layer = "repository-coroutine",
            message = "after delay in suspend repository function",
            details = mapOf("id" to id.toString()),
        )
        return ItemResponse(
            id = id,
            name = "item-$id",
            style = style,
            thread = DemoLog.threadName(),
        )
    }
}


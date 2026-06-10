package com.studycs.webfluxdemo.service

import com.studycs.webfluxdemo.model.ItemResponse
import com.studycs.webfluxdemo.repository.DemoRepository
import com.studycs.webfluxdemo.support.DemoLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ItemDemoService(
    private val demoRepository: DemoRepository,
) {
    fun getItemReactor(id: Int, requestId: String): Mono<ItemResponse> {
        // Reactor style:
        // - This method returns a description of async work.
        // - Work starts when WebFlux subscribes to the Mono.
        // - The caller does not call subscribe() in controller code because WebFlux is the subscriber.
        return demoRepository.reactorItem(id, requestId, "reactor-mono")
            .doOnSubscribe {
                DemoLog.mark(
                    requestId = requestId,
                    layer = "service-reactor",
                    message = "Mono subscribed; lazy pipeline starts now",
                    details = mapOf("id" to id.toString()),
                )
            }
    }

    fun getItemsReactor(requestId: String): Flux<ItemResponse> {
        // Flux is a lazy stream of 0..N values. WebFlux subscribes and asks for data.
        // doOnRequest shows demand/backpressure signals from the downstream subscriber.
        return Flux.range(1, 5)
            .doOnSubscribe {
                DemoLog.mark(
                    requestId = requestId,
                    layer = "service-reactor",
                    message = "Flux subscribed; item stream starts now",
                )
            }
            .doOnRequest { requested ->
                DemoLog.mark(
                    requestId = requestId,
                    layer = "service-reactor",
                    message = "downstream requested items",
                    details = mapOf("requested" to requested.toString()),
                )
            }
            .concatMap { id -> demoRepository.reactorItem(id, requestId, "reactor-flux") }
    }

    suspend fun getItemCoroutine(id: Int, requestId: String): ItemResponse {
        // Coroutine style:
        // - Calling a suspend function starts execution immediately in the current coroutine.
        // - When it reaches delay/non-blocking I/O, it suspends without blocking the thread.
        // - There is no subscribe() in user code; Spring bridges the suspend function to a Mono.
        DemoLog.mark(
            requestId = requestId,
            layer = "service-coroutine",
            message = "suspend function entered; execution has started",
            details = mapOf("id" to id.toString()),
        )
        return demoRepository.coroutineItem(id, requestId, "coroutine-suspend")
    }

    fun getItemsCoroutine(requestId: String): Flow<ItemResponse> {
        // Flow is cold/lazy like Flux. The block below starts only when WebFlux collects the Flow.
        // Each delay suspends the coroutine; it does not park a thread.
        return flow {
            DemoLog.mark(
                requestId = requestId,
                layer = "service-flow",
                message = "Flow collection started; cold Flow is now executing",
            )
            for (id in 1..5) {
                emit(demoRepository.coroutineItem(id, requestId, "coroutine-flow"))
            }
        }
    }

    fun flatMapConcurrency(requestId: String, count: Int, concurrency: Int): Flux<ItemResponse> {
        // flatMap subscribes to inner publishers concurrently.
        // The concurrency argument caps the number of in-flight inner Monos.
        return Flux.range(1, count)
            .doOnRequest { requested ->
                DemoLog.mark(
                    requestId = requestId,
                    layer = "flatmap-backpressure",
                    message = "downstream demand observed before flatMap",
                    details = mapOf("requested" to requested.toString()),
                )
            }
            .flatMap(
                { id ->
                    demoRepository.reactorItem(id, requestId, "flatmap-concurrency")
                        .doOnSubscribe {
                            DemoLog.mark(
                                requestId = requestId,
                                layer = "flatmap-backpressure",
                                message = "inner Mono subscribed",
                                details = mapOf("id" to id.toString()),
                            )
                        }
                },
                concurrency,
            )
    }
}


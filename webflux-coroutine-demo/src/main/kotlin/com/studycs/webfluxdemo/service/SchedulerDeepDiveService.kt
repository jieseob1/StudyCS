package com.studycs.webfluxdemo.service

import com.studycs.webfluxdemo.model.FusionDemoResponse
import com.studycs.webfluxdemo.model.SchedulerDemoResponse
import com.studycs.webfluxdemo.model.ThreadStep
import com.studycs.webfluxdemo.support.DemoLog
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import reactor.core.Fuseable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.Collections
import kotlin.system.measureTimeMillis

@Service
class SchedulerDeepDiveService {
    suspend fun subscribeOn(requestId: String): SchedulerDemoResponse {
        val recorder = StepRecorder(requestId)
        val result = Mono.fromCallable {
            recorder.record("source", "source callable starts after subscribeOn moves subscription upstream")
            "payload"
        }
            .map {
                recorder.record("map-before-subscribeOn", "this map belongs to the upstream chain")
                it.uppercase()
            }
            // subscribeOn affects where subscription and upstream source work start.
            // In this small chain, downstream maps also run on that worker because no later boundary moves them.
            .subscribeOn(Schedulers.boundedElastic())
            .map {
                recorder.record("map-after-subscribeOn", "no publishOn exists, so this usually stays on boundedElastic")
                "$it-from-subscribeOn"
            }
            .awaitSingle()

        return SchedulerDemoResponse(
            requestId = requestId,
            endpoint = "/demo/reactor/scheduler/subscribe-on",
            mode = "subscribeOn",
            result = result,
            steps = recorder.steps(),
            explanation = "subscribeOn changes the subscription/upstream execution thread. Use it when the source itself is blocking or expensive.",
        )
    }

    suspend fun publishOn(requestId: String): SchedulerDemoResponse {
        val recorder = StepRecorder(requestId)
        val result = Mono.fromCallable {
            recorder.record("source", "source runs on the current subscriber thread until publishOn boundary")
            "payload"
        }
            .map {
                recorder.record("before-publishOn", "still before the scheduler boundary")
                it.uppercase()
            }
            // publishOn moves downstream signal processing after this point.
            .publishOn(Schedulers.parallel())
            .map {
                recorder.record("after-publishOn", "downstream map runs on Schedulers.parallel")
                "$it-from-publishOn"
            }
            .awaitSingle()

        return SchedulerDemoResponse(
            requestId = requestId,
            endpoint = "/demo/reactor/scheduler/publish-on",
            mode = "publishOn",
            result = result,
            steps = recorder.steps(),
            explanation = "publishOn changes the thread used by downstream operators after the boundary.",
        )
    }

    suspend fun fusion(requestId: String): FusionDemoResponse {
        val fused = Flux.range(1, 10)
            .map { it + 1 }
            .filter { it % 2 == 0 }

        val hidden = Flux.range(1, 10)
            // hide deliberately masks the upstream implementation and disables fuseable optimizations.
            .hide()
            .map { it + 1 }
            .filter { it % 2 == 0 }

        val fusedResult = fused.collectList().awaitSingle()
        val hiddenResult = hidden.collectList().awaitSingle()

        DemoLog.mark(
            requestId = requestId,
            layer = "operator-fusion",
            message = "compared fuseable chain with hidden non-fuseable chain",
            details = mapOf(
                "fusedClass" to fused.javaClass.simpleName,
                "hiddenClass" to hidden.javaClass.simpleName,
            ),
        )

        return FusionDemoResponse(
            requestId = requestId,
            endpoint = "/demo/reactor/fusion",
            fusedIsFuseable = fused is Fuseable,
            hiddenIsFuseable = hidden is Fuseable,
            fusedClass = fused.javaClass.name,
            hiddenClass = hidden.javaClass.name,
            fusedResult = fusedResult,
            hiddenResult = hiddenResult,
            explanation = "Operator fusion is an internal optimization. hide() is useful for demos because it breaks fuseable optimization without changing the visible result.",
        )
    }

    private class StepRecorder(
        private val requestId: String,
    ) {
        private val startedAt = System.nanoTime()
        private val steps = Collections.synchronizedList(mutableListOf<ThreadStep>())

        fun record(name: String, detail: String) {
            val elapsedMs = (System.nanoTime() - startedAt) / 1_000_000
            val step = ThreadStep(
                name = name,
                thread = DemoLog.threadName(),
                elapsedMs = elapsedMs,
                detail = detail,
            )
            steps += step
            DemoLog.mark(
                requestId = requestId,
                layer = "scheduler-demo",
                message = name,
                details = mapOf("detail" to detail, "elapsedMs" to elapsedMs.toString()),
            )
        }

        fun steps(): List<ThreadStep> = steps.toList()
    }
}


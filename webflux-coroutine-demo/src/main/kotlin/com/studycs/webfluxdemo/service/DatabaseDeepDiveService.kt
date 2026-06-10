package com.studycs.webfluxdemo.service

import com.studycs.webfluxdemo.model.DbDemoResponse
import com.studycs.webfluxdemo.support.DemoLog
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import kotlin.system.measureTimeMillis

@Service
class DatabaseDeepDiveService(
    private val jdbcTemplate: JdbcTemplate,
    private val databaseClient: DatabaseClient,
) {
    @PostConstruct
    fun initDemoTables() {
        val createSql = """
            CREATE TABLE IF NOT EXISTS demo_item (
                id INT PRIMARY KEY,
                name VARCHAR(50) NOT NULL
            )
        """.trimIndent()

        jdbcTemplate.execute(createSql)
        jdbcTemplate.update("DELETE FROM demo_item")
        jdbcTemplate.batchUpdate(
            "INSERT INTO demo_item(id, name) VALUES (?, ?)",
            listOf(
                arrayOf<Any>(1, "alpha"),
                arrayOf<Any>(2, "bravo"),
                arrayOf<Any>(3, "charlie"),
            ),
        )

        // R2DBC has its own connection factory. Initialize through DatabaseClient as well so the
        // endpoint works even if the driver does not share the JDBC in-memory connection.
        databaseClient.sql(createSql).fetch().rowsUpdated().block()
        databaseClient.sql("DELETE FROM demo_item").fetch().rowsUpdated().block()
        databaseClient.sql("INSERT INTO demo_item(id, name) VALUES (1, 'alpha'), (2, 'bravo'), (3, 'charlie')")
            .fetch()
            .rowsUpdated()
            .block()
    }

    suspend fun jdbcBad(requestId: String, ms: Long): DbDemoResponse {
        var rowCount = 0
        val beforeThread = DemoLog.threadName()
        val elapsedMs = measureTimeMillis {
            DemoLog.mark(
                requestId = requestId,
                layer = "jdbc-bad",
                message = "BAD: simulating slow blocking JDBC work on the request thread",
                details = mapOf("sleepMs" to ms.toString()),
            )
            Thread.sleep(ms)
            rowCount = countWithJdbc()
        }

        return DbDemoResponse(
            requestId = requestId,
            endpoint = "/demo/db/jdbc/bad",
            accessStyle = "jdbc-blocking-on-request-thread",
            rowCount = rowCount,
            elapsedMs = elapsedMs,
            thread = DemoLog.threadName(),
            details = mapOf(
                "beforeThread" to beforeThread,
                "warning" to "JDBC is blocking. Do not run it directly on reactor-http-nio.",
            ),
        )
    }

    suspend fun jdbcOffload(requestId: String, ms: Long): DbDemoResponse {
        val beforeThread = DemoLog.threadName()
        var blockingThread = ""
        lateinit var result: Pair<Int, Long>

        val elapsedMs = measureTimeMillis {
            result = Mono.fromCallable {
                blockingThread = DemoLog.threadName()
                DemoLog.mark(
                    requestId = requestId,
                    layer = "jdbc-offload",
                    message = "blocking JDBC work moved to boundedElastic",
                    details = mapOf("sleepMs" to ms.toString()),
                )
                Thread.sleep(ms)
                countWithJdbc()
            }
                .subscribeOn(Schedulers.boundedElastic())
                .map { count -> count to ms }
                .awaitSingle()
        }

        return DbDemoResponse(
            requestId = requestId,
            endpoint = "/demo/db/jdbc/offload",
            accessStyle = "jdbc-blocking-offloaded",
            rowCount = result.first,
            elapsedMs = elapsedMs,
            thread = DemoLog.threadName(),
            details = mapOf(
                "beforeThread" to beforeThread,
                "blockingThread" to blockingThread,
                "scheduler" to "Schedulers.boundedElastic",
            ),
        )
    }

    suspend fun r2dbc(requestId: String, ms: Long): DbDemoResponse {
        var rowCount = 0
        val beforeThread = DemoLog.threadName()
        val elapsedMs = measureTimeMillis {
            DemoLog.mark(
                requestId = requestId,
                layer = "r2dbc",
                message = "simulated remote wait uses delay; database query uses R2DBC publisher",
                details = mapOf("delayMs" to ms.toString()),
            )
            delay(ms)
            rowCount = databaseClient.sql("SELECT COUNT(*) AS row_count FROM demo_item")
                .map { row, _ -> (row.get("row_count") as Number).toInt() }
                .one()
                .awaitSingle()
        }

        return DbDemoResponse(
            requestId = requestId,
            endpoint = "/demo/db/r2dbc",
            accessStyle = "r2dbc-non-blocking-publisher",
            rowCount = rowCount,
            elapsedMs = elapsedMs,
            thread = DemoLog.threadName(),
            details = mapOf(
                "beforeThread" to beforeThread,
                "bridge" to "DatabaseClient publisher + coroutine awaitSingle",
            ),
        )
    }

    private fun countWithJdbc(): Int =
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM demo_item", Int::class.java) ?: 0
}


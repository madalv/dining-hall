package com.madalv

import mu.KotlinLogging
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.madalv.plugins.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.engine.cio.*
import io.ktor.client.statement.*
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.coroutines.launch
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random.Default.nextInt

private val client = HttpClient(CIO) {
    install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
}
private val logger = KotlinLogging.logger {}

fun main() {
    embeddedServer(Netty, port = 8081, host = "0.0.0.0") {
        configureRouting()
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }

        for(i in 1..10) {
            launch(CoroutineName("sendingOrder")) {

                val order: Order = generateOrder()

                // send order to kitchen
                client.post {
                    url {
                        protocol = URLProtocol.HTTP
                        host = cfg.kitchen
                        path("/order")
                        port = 8082
                    }
                    contentType(ContentType.Application.Json)
                    setBody(order)
                }
                logger.debug { "Sending order ${order.id} to KITCHEN" }
            }
        }

    }.start(wait = true)
}

fun generateOrder() : Order {

    val r = ThreadLocalRandom.current()
    val id: Int = r.nextInt(0, cfg.orderIdMax)
    val itemNr: Int = r.nextInt(1, cfg.maxItemsPerOrder)
    val items: List<Int> = List(itemNr) {r.nextInt(0, 11)}
    val prepTime: Int = r.nextInt(10, 60)
    val time: Long = System.currentTimeMillis() / 1000

    val order: Order = Order(id, items, 1, prepTime * cfg.waitTimeCoeff, time)

    return order
}
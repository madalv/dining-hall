package com.madalv

import mu.KotlinLogging
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.madalv.plugins.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.server.application.*
import kotlinx.coroutines.launch
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicInteger

val client = HttpClient(CIO) {
    install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
}
 val logger = KotlinLogging.logger {}

// used to send orders from tables to waiters
val sendToWaitersChannel = Channel<Order>()
val ratingChannel = Channel<Int>()
val tables = mutableListOf<Table>()
val waiters = mutableListOf<Waiter>()
var rating = AtomicInteger(0)
var nrOrders = AtomicInteger(0)

suspend fun calculateRating() {
    while (true) {
        val ratingCurrent = ratingChannel.receive()
        nrOrders.incrementAndGet()
        rating.addAndGet(ratingCurrent)
        logger.debug { "------------------------------------------------------- AVG RATING: ${rating.get().toDouble() / nrOrders.get()}"  }
    }
}

fun main() {
    embeddedServer(Netty, port = 8081, host = "0.0.0.0") {
        configureRouting()
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }

        for (i in 0 until Cfg.nrTables) {
            val table = Table(i)
            tables.add(table)
            launch {
                table.use()
            }
        }

        for (i in 0 until Cfg.nrWaiters) {
            val waiter = Waiter(i)
            waiters.add(waiter)
            launch {
                waiter.pickupOrder()
            }
            launch {
                waiter.distributeOrder()
            }
        }

        launch {
            calculateRating()
        }

    }.start(wait = true)
}



package com.madalv

import com.madalv.plugins.configureRouting
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = cfg.port, host = "0.0.0.0") {
        configureRouting()
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }

        for (i in 0 until cfg.nrTables) {
            val table = Table(i)
            tables.add(table)
            launch {
                table.use()
            }
        }

        for (i in 0 until cfg.nrWaiters) {
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

        launch {
            registerRestaurant()
        }

    }.start(wait = true)
}



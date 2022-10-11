package com.madalv

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.io.File
import java.util.concurrent.atomic.AtomicInteger


val configJson: String =
File("config/config.json").inputStream().readBytes().toString(Charsets.UTF_8)

val cfg: Config = Json.decodeFromString(Config.serializer(), configJson)

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
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

val menuJson: String =
    File("config/menu.json").inputStream().readBytes().toString(Charsets.UTF_8)
val menu = Json { coerceInputValues = true }.decodeFromString(ListSerializer(Food.serializer()), menuJson)
suspend fun calculateRating() {
    while (true) {
        val ratingCurrent = ratingChannel.receive()
        nrOrders.incrementAndGet()
        rating.addAndGet(ratingCurrent)
        logger.debug {
            "------------------------------------------------------- AVG RATING: ${
                rating.get().toDouble() / nrOrders.get()
            }"
        }
    }
}

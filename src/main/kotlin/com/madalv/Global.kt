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

object Cfg {
    const val host = "localhost"
    const val timeUnit = 100
    const val nrTables = 10
    const val nrWaiters = 4
    const val maxItemsPerOrder = 10
    const val orderIdMax = 500
    const val waiterPickupTimeMax: Long = 4
    const val waiterPickupTimeMin: Long = 2


    const val maxTableWait: Long = 60
    const val minTableWait: Long = 10
    const val waitTimeCoefficient = 1.3
}

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
    File("src/main/kotlin/com/madalv/config/menu.json").inputStream().readBytes().toString(Charsets.UTF_8)
val menu = Json { coerceInputValues = true }.decodeFromString(ListSerializer(Food.serializer()), menuJson)
suspend fun calculateRating() {
    while (true) {
        val ratingCurrent = ratingChannel.receive()
        nrOrders.incrementAndGet()
        rating.addAndGet(ratingCurrent)
        logger.debug { "------------------------------------------------------- AVG RATING: ${rating.get().toDouble() / nrOrders.get()}"  }
    }
}

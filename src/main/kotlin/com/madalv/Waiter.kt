package com.madalv

import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.util.concurrent.ThreadLocalRandom

class Waiter(private val id: Int) {
    // used to send distribute orders to waiters from kitchen
    val distributionChannel = Channel<DetailedOrder>()

    suspend fun pickupOrder() {
        for (order in sendToWaitersChannel) {
            talkToClient()
            logger.debug { "Waiter $id picked up order ${order.id} from table ${order.tableId}!" }
            order.waiterId = id
            order.pickupTime = System.currentTimeMillis()
            sendOrder(order)
            logger.debug { "Waiter $id sent order ${order.id} to Kitchen!" }
        }
    }

    suspend fun distributeOrder() {
        for (order in distributionChannel) {
            logger.debug { "Waiter $id got order ${order.id} & took it to table ${order.tableId}!" }
            tables[order.tableId].receiveOrderChannel.send(order)
        }
    }

    private suspend fun talkToClient() {
        val time: Long = ThreadLocalRandom.current()
            .nextLong(cfg.waiterPickupTimeMin, cfg.waiterPickupTimeMax + 1)
        delay(time * cfg.timeUnit)
    }

    private suspend fun sendOrder(order: Order) {
        println(cfg.kitchen)
        client.post("http://${cfg.kitchen}/order") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToJsonElement(order))
        }
    }
}
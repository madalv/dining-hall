package com.madalv

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import java.util.concurrent.ThreadLocalRandom

class Table(
    var state: TableState,
    val id: Int) {

    suspend fun use() {
        while (true) {
            val time: Long = ThreadLocalRandom.current().nextLong(5, Cfg.maxTableWait + 1)
            delay(time * Cfg.timeUnit)

            val order = generateOrder()
            sendOrder(order)
        }
    }

    private suspend fun sendOrder(order: Order) {

        client.post {
            url {
                protocol = URLProtocol.HTTP
                host = Cfg.host
                path("/order")
                port = 8082
            }
            contentType(ContentType.Application.Json)
            setBody(order)
        }
        state = TableState.WAITING_FOR_ORDER
        logger.debug { "Sending order ${order.id} to KITCHEN. Table $id WAITING FOR ORDER" }
    }

    private fun generateOrder() : Order {

        val r = ThreadLocalRandom.current()
        val idOrder: Int = r.nextInt(0, Cfg.orderIdMax)
        val itemNr: Int = r.nextInt(1, Cfg.maxItemsPerOrder)
        val items: List<Int> = List(itemNr) {r.nextInt(0, 11)}
        val prepTime: Int = r.nextInt(10, 60)
        val time: Long = System.currentTimeMillis() / 1000
        val order: Order = Order(idOrder, id, 0, items, 1, prepTime * Cfg.waitTimeCoeff, time)

        logger.debug { "Table $id generated order ${order.id}, now WAITING TO ORDER" }
        state = TableState.WAITING_TO_ORDER
        return order
    }
}
package com.madalv

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import java.util.concurrent.ThreadLocalRandom

class Table(
    private val id: Int
) {
    val receiveOrderChannel: Channel<Order> = Channel()
    private var currentOrder = Order(-5, -5, listOf(),-5, -5, -5.0 )

    suspend fun use() {
        while (true) {
            wait()
            val order = generateOrder()
            //state = TableState.WAITING_TO_ORDER
            currentOrder = order
            logger.debug { "TABLE $id has order ${order.id}!" }
            sendToWaitersChannel.send(order)
            //state = TableState.WAITING_FOR_ORDER
            receiveOrder()
            //state = TableState.FREE
        }
    }

    private suspend fun receiveOrder() {

        val order = receiveOrderChannel.receive()
        val servingTime = System.currentTimeMillis()
        val waitTime = servingTime - order.pickupTime
        var rating = 0

        if (waitTime <= order.maxWait * Cfg.timeUnit) rating = 5
        else if (waitTime <= order.maxWait * 1.1 * Cfg.timeUnit) rating = 4
        else if (waitTime <= order.maxWait * 1.2 * Cfg.timeUnit) rating = 3
        else if (waitTime <= order.maxWait * 1.3 * Cfg.timeUnit) rating = 2
        else if (waitTime <= order.maxWait * 1.4 * Cfg.timeUnit) rating = 1

        ratingChannel.send(rating)

        if (currentOrder.id == order.id)
            logger.debug { "Order ${order.id} received by Table $id! RATING $rating WAITIME $waitTime MAXWAIT ${order.maxWait * Cfg.timeUnit}" }
        else
            logger.debug { "ORDER MISMATCH: table $id sent order $currentOrder.id but got ${order.id}." }
    }


    private suspend fun wait() {
        val time: Long = ThreadLocalRandom.current().nextLong(5, Cfg.maxTableWait + 1)
        delay(time * Cfg.timeUnit)
    }

    private fun generateOrder(): Order {

        val r = ThreadLocalRandom.current()
        val idOrder: Int = r.nextInt(0, Cfg.orderIdMax)
        val itemNr: Int = r.nextInt(1, Cfg.maxItemsPerOrder)
        val items: List<Int> = List(itemNr) { r.nextInt(1, menu.size + 1) }
        val time: Long = System.currentTimeMillis()
        val priority = menu.size - itemNr

        var prepTimeMax = 0
        for (foodId in items) {
            if (menu[foodId - 1].preparationTime > prepTimeMax)
                prepTimeMax = menu[foodId - 1].preparationTime
        }

        return Order(idOrder, id, items, priority, time, prepTimeMax * Cfg.waitTimeCoefficient)
    }
}
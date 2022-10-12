package com.madalv

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import java.util.concurrent.ThreadLocalRandom

class Table(
    private val id: Int
) {
    val receiveOrderChannel: Channel<DetailedOrder> = Channel()
    private var currentOrder = Order(listOf(), -5, -5.0, -5, -5, -5, -5, -5)

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
        val waitTime = order.cookingTime
        var rating = 0

        if (waitTime <= order.maxWait * cfg.timeUnit) rating = 5
        else if (waitTime <= order.maxWait * 1.1 * cfg.timeUnit) rating = 4
        else if (waitTime <= order.maxWait * 1.2 * cfg.timeUnit) rating = 3
        else if (waitTime <= order.maxWait * 1.3 * cfg.timeUnit) rating = 2
        else if (waitTime <= order.maxWait * 1.4 * cfg.timeUnit) rating = 1

        ratingChannel.send(rating)

        if (currentOrder.id == order.id)
            logger.debug { "Order ${order.id} received by Table $id! RATING $rating WAITIME $waitTime MAXWAIT ${order.maxWait * cfg.timeUnit}" }
        else
            logger.debug { "ORDER MISMATCH: table $id sent order $currentOrder.id but got ${order.id}." }
    }


    private suspend fun wait() {
        val time: Long = ThreadLocalRandom.current().nextLong(cfg.minTableWait, cfg.maxTableWait + 1)
        delay(time * cfg.timeUnit)
    }

    private fun generateOrder(): Order {

        val r = ThreadLocalRandom.current()
        val idOrder: Int = r.nextInt(0, cfg.orderIdMax)
        var itemNr: Int = r.nextInt(1, cfg.maxItemsPerOrder)
        val items: List<Int> = List(itemNr) { r.nextInt(1, menu.size + 1) }
        val time: Long = System.currentTimeMillis()

        for (i in 0 until 3) {
            if (itemNr > 5) itemNr = r.nextInt(1, cfg.maxItemsPerOrder)
            else break
        }

        var prepTimeMax: Long = 0
        for (foodId in items) {
            if (menu[foodId - 1].preparationTime > prepTimeMax)
                prepTimeMax = menu[foodId - 1].preparationTime
        }

        val priority = menu.size - itemNr
        //val priority = 100 - prepTimeMax.toInt()


        return Order(items, priority, prepTimeMax * cfg.waitTimeCoefficient, time, idOrder, id, -5, -5)
    }
}
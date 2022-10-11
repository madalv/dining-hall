package com.madalv.plugins

import com.madalv.DetailedOrder
import com.madalv.Order
import com.madalv.waiters
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging

val logger = KotlinLogging.logger {}
fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello Dining Hall!")
        }
        post("/distribution") {
            val order: DetailedOrder = call.receive()
            logger.debug { "------------------ GOT ORDER ${order.id} AT DISTRIBUTION POINT ------------------" }
            waiters[order.waiterId].distributionChannel.send(order)
        }
    }
}

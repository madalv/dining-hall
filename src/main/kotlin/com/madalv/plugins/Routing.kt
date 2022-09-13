package com.madalv.plugins

import com.madalv.Order
import com.madalv.logger
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.launch

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello Dining Hall!")
        }
        post("/distribution") {
            launch {
                val order: Order = call.receive()
                logger.debug("Order ${order.id} ready to be served! Table ${order.tableId} FREE")
            }
        }
    }
}

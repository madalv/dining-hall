package com.madalv.plugins

import com.madalv.Order
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello Dining Hall!")
        }
        post("/order") {
            val order: Order = call.receive()
            println(order)
        }
    }
}

package com.madalv.plugins

import com.madalv.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import mu.KotlinLogging
import java.util.concurrent.ThreadLocalRandom

val logger = KotlinLogging.logger {}
fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello Dining Hall!")
        }
        // GOT ORDER BACK FROM KITCHEN
        post("/distribution") {
            val order: DetailedOrder = call.receive()
            if (order.waiterId == -5) {
                logger.debug { "------------------ TAKEOUT ${order.id} READY ------------------" }
            } else {
                logger.debug { "------------------ GOT ORDER ${order.id} AT DISTRIBUTION POINT ------------------" }
                waiters[order.waiterId].distributionChannel.send(order)
            }
        }

        // lab 2 BS
        route("/v2") {
            // CLIENT CHECKS IF ORDER IS READY
            get("/order/{id}") {
            }
            // ORDERING SERVICE SENDS NEW ORDER
            post("/order") {
                launch {
                    val torder: TakeoutOrder = call.receive()
                    val order = Order(torder.items,
                        torder.priority,
                        torder.maxWait,
                        torder.createdTime,
                        ThreadLocalRandom.current().nextInt(0, cfg.orderIdMax),
                        -5, -5, -5
                    )
                    val takeoutResponse = TakeoutResponse(
                        order.id, cfg.restaurantID, cfg.address,
                        69.0, order.createdTime, System.currentTimeMillis()
                    )
                    call.respond(takeoutResponse)
                    logger.debug { " ---- GOT TAEKOUT $order, sending to kitchen!" }
                    client.post("http://${cfg.kitchen}/order") {
                        contentType(ContentType.Application.Json)
                        setBody(Json.encodeToJsonElement(Json.encodeToJsonElement(order)))
                    }
                }
            }
        }
    }
}

package com.madalv.plugins

import com.madalv.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

        // TODO finish: client gets order, restaurant order map and retrieval
        // TODO check if client code works
        // TODO rating :c

        // lab 2 BS
        route("/v2") {
            // CLIENT CHECKS IF ORDER IS READY
            get("/order/{id}") {

            }
            // ORDERING SERVICE SENDS NEW ORDER
            post("/order") {
                launch {
                    val torder: TakeoutOrder = call.receive()
                    val order = Order(torder.items, torder.priority, torder.maxWait, torder.createdTime,
                        ThreadLocalRandom.current().nextInt(0, cfg.orderIdMax),
                        -5, -5, -5
                    )

                    logger.debug { " ---- GOT TAEKOUT $order, sending to kitchen!" }

                    var takeoutResponse: TakeoutResponse?
                    runBlocking {
                        val response = async { getKitchenRespone(order) }

                        val kitchenResponse = response.await()
                        logger.debug { kitchenResponse }


//                        client.post("http://${cfg.kitchen}/order") {
//                            contentType(ContentType.Application.Json)
//                            setBody(Json.encodeToJsonElement(order))
//                        }

                        takeoutResponse = TakeoutResponse(
                            order.id, cfg.restaurantID, cfg.address,
                            calculateEstimatedWait(kitchenResponse, order), order.createdTime, System.currentTimeMillis()
                        )
                    }
                    call.respond(takeoutResponse!!)
                }
            }
        }
    }
}

suspend fun getKitchenRespone(order: Order): OrderResponse {
    val response: OrderResponse = client.post("http://${cfg.kitchen}/order") {
        contentType(ContentType.Application.Json)
        setBody(order)
    }.body()

    return response
}

fun calculateEstimatedWait(response: OrderResponse, order: Order): Double {
    var A = 0.0
    for (item in order.items)
        if (menu[item - 1].cookingApparatus == null) A++
    val B = response.sumProeficiency.toDouble()
    val C = order.items.size - A
    val D = response.nrCookingApp.toDouble()
    val E: Double = (response.nrWaitingOrders * cfg.maxItemsPerOrder).toDouble()
    val F = order.items.size.toDouble()
    return (A / B + C / D) * (E + F) / F
}
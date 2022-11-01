package com.madalv.plugins

import com.madalv.*
import com.madalv.lab2logic.DetailedTakeout
import com.madalv.lab2logic.Rating
import com.madalv.lab2logic.RestaurantResponse
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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom

val logger = KotlinLogging.logger {}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello Dining Hall Restaurant ${cfg.name}!")
        }
        // GOT ORDER BACK FROM KITCHEN
        post("/distribution") {
            val order: DetailedOrder = call.receive()
            if (order.waiterId == -5) {
                val response = responses.getValue(order.id)
                readyTakeouts[order.id] = DetailedTakeout(order.id, true, order.priority, response.estimatedWait,
                    response.createdTime, response.registeredTime, order.cookingTime, order.cookingTime, order.maxWait,
                    order.orderItems
                )
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
                launch {
                    val id: Int? = call.parameters["id"]?.toInt()
                    logger.debug { " - - Client has come to pickup takeout $id" }
                    if (readyTakeouts.containsKey(id)) {
                        logger.debug { " - - Takeout $id ready!" }
                        call.respond(readyTakeouts[id]!!)
                        responses.remove(id)
                        readyTakeouts.remove(id)
                    } else {
                        logger.debug { " - - Come back later!" }
                        call.respond(DetailedTakeout(id!!, false, 0, responses.getValue(id).estimatedWait,
                            0, 0, 0, 0, 0.0, mutableListOf()))
                    }
                }
            }
            // ORDERING SERVICE SENDS NEW ORDER
            post("/order") {
                launch {
                    var takeoutResponse: TakeoutResponse?
                    runBlocking {
                        val torder: TakeoutOrder = call.receive()
                        val order = Order(torder.items, torder.priority, torder.maxWait, torder.createdTime,
                            ThreadLocalRandom.current().nextInt(0, cfg.orderIdMax),
                            -5, -5, -5
                        )

                        logger.debug { " ---- GOT TAEKOUT $order, sending to kitchen!" }
                        val response = async { getKitchenRespone(order) }
                        val kitchenResponse = response.await()

                        takeoutResponse = TakeoutResponse(order.id, cfg.restaurantID, cfg.address,
                            calculateEstimatedWait(kitchenResponse, order), order.createdTime, System.currentTimeMillis()
                        )
                        responses[order.id] = takeoutResponse!!
                    }
                    call.respond(takeoutResponse!!)
                }
            }

            post("/rating") {
                val rating: Rating = call.receive()
                ratingChannel.send(rating.rating)
                call.respond(RestaurantResponse(cfg.restaurantID, avgRating, nrOrders.get()))
            }
        }
    }
}
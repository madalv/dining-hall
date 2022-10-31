package com.madalv.lab2logic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rating(
    @SerialName("restaurant_id") val restaurantId: Int,
    @SerialName("rating") val rating: Int,
    @SerialName("order_id") val orderId: Int,
    @SerialName("estimated_waiting_time") val estimatedWait: Double,
    @SerialName("waiting_time") val waitingTime: Long,
)

@Serializable
data class RestaurantResponse(
    @SerialName("restaurant_id") val restaurantId: Int,
    @SerialName("restaurant_avg_rating") val resAvgRating: Double,
    @SerialName("prepared_orders") val preparedOrders: Int
)
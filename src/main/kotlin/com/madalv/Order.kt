package com.madalv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val items: List<Int>,
    val priority: Int,
    @SerialName("max_wait") var maxWait: Double,
    @SerialName("created_time") var createdTime: Long,
    @SerialName("order_id") var id: Int?,
    @SerialName("table_id") var tableId: Int?,
    @SerialName("pick_up_time") var pickupTime: Long?,
    @SerialName("waiter_id") var waiterId: Int?
)

@Serializable
data class TakeoutResponse(
    @SerialName("order_id") val id: Int,
    @SerialName("restaurant_id") val restaurantID: Int,
    @SerialName("restaurant_address") val resAddress: String,
    @SerialName("estimated_waiting_time") val estimatedWait: Double,
    @SerialName("created_time") val createdTime: Long,
    @SerialName("registered_time") val registeredTime: Long
)
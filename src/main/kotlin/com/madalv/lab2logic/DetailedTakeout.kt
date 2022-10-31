package com.madalv.lab2logic

import com.madalv.OrderItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetailedTakeout (
    @SerialName("order_id") val id: Int,
    @SerialName("is_ready") val isReady: Boolean,
    val priority: Int,
    @SerialName("estimated_waiting_time") val estimatedWait: Double,
    @SerialName("created_time") val createdTime: Long,
    @SerialName("registered_time") val registeredTime: Long,
    @SerialName("prepared_time") val preparedTime: Long,
    @SerialName("cooking_time") val cookingTime: Long,
    @SerialName("max_wait") var maxWait: Double,
    @SerialName("cooking_details") var orderItems: MutableList<OrderItem>
)
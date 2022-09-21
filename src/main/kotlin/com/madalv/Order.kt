package com.madalv

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Order( @SerialName("order_id") val id: Int,
                  @SerialName("table_id") val tableId: Int,
                  @SerialName("waiter_id") val waiterId: Int,
                 val items: List<Int>,
                 val priority: Int,
                 @SerialName("max_wait") val maxWait: Double,
                 @SerialName("pick_up_time") val pickupTime: Long)

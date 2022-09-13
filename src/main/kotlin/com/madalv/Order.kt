package com.madalv

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Order(val id: Int,
                 val items: List<Int>,
                 val priority: Int,
                 @SerialName("max_wait")
                 val maxWait: Double,
                 @SerialName("pick_up_time")
                 val pickupTime: Long)

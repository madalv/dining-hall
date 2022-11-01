package com.madalv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderResponse(
    @SerialName("sum_cook_proef") val sumProeficiency: Int,
    @SerialName("nr_waiting_orders") val nrWaitingOrders: Int,
    @SerialName("nr_cooking_app") val nrCookingApp: Int
)
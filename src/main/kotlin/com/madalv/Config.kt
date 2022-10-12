package com.madalv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Config(
    val kitchen: String,
    @SerialName("timeunit") val timeUnit: Long,
    @SerialName("nr_waiters") val nrWaiters: Int,
    @SerialName("nr_tables") val nrTables: Int,
    @SerialName("max_items_order") val maxItemsPerOrder: Int,
    @SerialName("max_orderid") val orderIdMax: Int,
    @SerialName("min_waiter_pickuptime") val waiterPickupTimeMin: Long,
    @SerialName("max_waiter_pickuptime") val waiterPickupTimeMax: Long,
    @SerialName("min_tablewait") val minTableWait: Long,
    @SerialName("max_tablewait") val maxTableWait: Long,
    @SerialName("waitime_coefficient") val waitTimeCoefficient: Double,
    @SerialName("restaurant_id") val restaurantID: Int,
    val name: String,
    val port: Int,
    val host: String,
    @SerialName("ordering_service") val orderingService: String,
    val address: String
)
package com.madalv

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Order(@SerialName("order_id") val id: Int,
                 @SerialName("table_id") val tableId: Int,
                 val items: List<Int>,
                 val priority: Int,
                 @SerialName("pick_up_time") var pickupTime: Long,
                 @SerialName("max_wait") var maxWait: Double
) {
    @SerialName("waiter_id") var waiterId: Int = -5
}

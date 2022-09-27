package com.madalv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
class Food(
    val id: Int,
    val name: String,
    @SerialName("preparation-time") val preparationTime: Long,
    val complexity: Int,
    @SerialName("cooking-apparatus") val cookingApparatus: String? = null
) {
    override fun toString(): String {
        return "ID $id PREP $preparationTime CMPLX $complexity APP $cookingApparatus"
    }
}


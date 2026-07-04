package com.iqbalfauzi.kitchenstockmobile.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListDto(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("product_id")
    val productId: String,
    val quantity: Double,
    @SerialName("is_bought")
    val isBought: Boolean = false,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

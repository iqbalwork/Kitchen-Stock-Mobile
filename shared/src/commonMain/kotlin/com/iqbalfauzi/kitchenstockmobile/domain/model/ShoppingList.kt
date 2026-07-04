package com.iqbalfauzi.kitchenstockmobile.domain.model

import kotlinx.datetime.Instant

data class ShoppingListItem(
    val id: String,
    val productId: String,
    val quantity: Double,
    val isBought: Boolean,
    val createdAt: Instant?,
    val updatedAt: Instant?,
    val product: Product? = null
)

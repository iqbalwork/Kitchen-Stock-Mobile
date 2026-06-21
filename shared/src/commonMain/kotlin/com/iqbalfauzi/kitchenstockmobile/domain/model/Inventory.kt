package com.iqbalfauzi.kitchenstockmobile.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class Category(
    val id: String,
    val name: String,
    val icon: String? = null
)

data class StorageLocation(
    val id: String,
    val name: String,
    val description: String? = null
)

data class Product(
    val id: String,
    val categoryId: String?,
    val name: String,
    val barcode: String? = null,
    val unit: String,
    val minStockLevel: Double? = null,
    val imageUrl: String? = null,
    val category: Category? = null
)

data class InventoryItem(
    val id: String,
    val productId: String,
    val storageLocationId: String,
    val quantity: Double,
    val expiryDate: LocalDate?,
    val updatedAt: Instant,
    val product: Product?,
    val location: StorageLocation?
)

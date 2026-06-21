package com.iqbalfauzi.kitchenstockmobile.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: String,
    @SerialName("user_id")
    val userId: String? = null,
    val name: String,
    val icon: String? = null
)

@Serializable
data class StorageLocationDto(
    val id: String,
    @SerialName("user_id")
    val userId: String? = null,
    val name: String,
    val description: String? = null
)

@Serializable
data class ProductDto(
    val id: String,
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("category_id")
    val categoryId: String? = null,
    val name: String,
    val barcode: String? = null,
    val unit: String,
    @SerialName("min_stock_level")
    val minStockLevel: Double? = null,
    @SerialName("image_url")
    val imageUrl: String? = null
)

@Serializable
data class InventoryDto(
    val id: String,
    @SerialName("product_id")
    val productId: String,
    @SerialName("storage_location_id")
    val storageLocationId: String,
    val quantity: Double,
    @SerialName("expiry_date")
    val expiryDate: String? = null,
    @SerialName("updated_at")
    val updatedAt: String
)

package com.iqbalfauzi.kitchenstock.presentation.inventory_detail.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.iqbalfauzi.kitchenstock.domain.model.Category
import com.iqbalfauzi.kitchenstock.domain.model.Product
import com.iqbalfauzi.kitchenstock.domain.model.StorageLocation as StorageLocationDomain

data class InventoryDetailUiState(
    val id: String? = null,
    val productId: String = "",
    val name: String = "",
    val categoryId: String = "",
    val storageLocationId: String = "",
    val location: String = "Fridge",
    val quantity: Double = 1.0,
    val unit: String = "Units",
    val minStockLevel: Double = 0.0,
    val expiryDate: String = "",
    val products: List<Product> = emptyList(),
    val locations: List<StorageLocationDomain> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false
)

data class StorageLocation(
    val name: String,
    val icon: ImageVector
)

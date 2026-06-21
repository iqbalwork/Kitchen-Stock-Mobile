package com.iqbalfauzi.kitchenstockmobile.presentation.inventory_detail.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.iqbalfauzi.kitchenstockmobile.domain.model.Product
import com.iqbalfauzi.kitchenstockmobile.domain.model.StorageLocation as StorageLocationDomain

data class InventoryDetailUiState(
    val id: String? = null,
    val productId: String = "",
    val name: String = "",
    val storageLocationId: String = "",
    val location: String = "Fridge",
    val quantity: Int = 1,
    val unit: String = "Units",
    val expiryDate: String = "",
    val products: List<Product> = emptyList(),
    val locations: List<StorageLocationDomain> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false
)

data class StorageLocation(
    val name: String,
    val icon: ImageVector
)

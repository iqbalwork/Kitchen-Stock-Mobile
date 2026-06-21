package com.iqbalfauzi.kitchenstockmobile.presentation.inventory_detail.model

import androidx.compose.ui.graphics.vector.ImageVector

data class InventoryDetailUiState(
    val id: String? = null,
    val name: String = "",
    val location: String = "Fridge",
    val quantity: Int = 1,
    val unit: String = "Units",
    val expiryDate: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false
)

data class StorageLocation(
    val name: String,
    val icon: ImageVector
)

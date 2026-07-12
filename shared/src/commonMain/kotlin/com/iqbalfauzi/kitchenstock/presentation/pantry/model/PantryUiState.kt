package com.iqbalfauzi.kitchenstock.presentation.pantry.model

import androidx.compose.ui.graphics.vector.ImageVector

import com.iqbalfauzi.kitchenstock.domain.model.StorageLocation

data class PantryUiState(
    val categories: List<StorageLocation> = emptyList(),
    val selectedCategoryId: String? = null,
    val groupedItems: Map<String, List<PantryItem>> = emptyMap(),
    val isRefreshing: Boolean = false
)

data class PantryItem(
    val id: String,
    val name: String,
    val quantity: Int,
    val unit: String,
    val location: String,
    val icon: ImageVector,
    val expiryStatus: ExpiryStatus
)

sealed interface ExpiryStatus {
    val label: String

    data class Warning(override val label: String) : ExpiryStatus
    data class Critical(override val label: String) : ExpiryStatus
    data class Normal(override val label: String = "Good") : ExpiryStatus
}

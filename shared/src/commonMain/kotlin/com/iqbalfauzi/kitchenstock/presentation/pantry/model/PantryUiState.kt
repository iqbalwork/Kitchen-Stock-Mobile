package com.iqbalfauzi.kitchenstock.presentation.pantry.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.iqbalfauzi.kitchenstock.domain.model.Category
import com.iqbalfauzi.kitchenstock.domain.model.StorageLocation

data class PantryUiState(
    val categories: List<StorageLocation> = emptyList(),
    val selectedCategoryId: String? = null,
    val groupedItems: Map<String, List<PantryItem>> = emptyMap(),
    val items: List<PantryItem> = emptyList(),
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val expiredCount: Int = 0,
    val nearExpiryCount: Int = 0,
    val totalStockCount: Int = 0,
    val isAddSheetVisible: Boolean = false,
    val storageLocations: List<StorageLocation> = emptyList(),
    val availableCategories: List<Category> = emptyList()
)

data class PantryItem(
    val id: String,
    val name: String,
    val quantity: Int,
    val unit: String,
    val location: String,
    val category: String = "",
    val icon: ImageVector,
    val expiryStatus: ExpiryStatus
)

sealed interface ExpiryStatus {
    val label: String

    data class Safe(override val label: String = "Aman") : ExpiryStatus
    data class NearExpiry(override val label: String) : ExpiryStatus
    data class Expired(override val label: String) : ExpiryStatus
}


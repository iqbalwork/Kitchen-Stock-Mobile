package com.iqbalfauzi.kitchenstockmobile.presentation.home.model

import androidx.compose.ui.graphics.vector.ImageVector

data class HomeUiState(
    val totalItems: Int = 0,
    val expiringCount: Int = 0,
    val outOfStockCount: Int = 0,
    val attentionItems: List<AttentionItem> = emptyList()
)

data class AttentionItem(
    val id: String,
    val name: String,
    val detail: String,
    val status: AttentionStatus,
    val icon: ImageVector
)

sealed interface AttentionStatus {
    val label: String

    data class Expiring(override val label: String) : AttentionStatus
    data class OutOfStock(override val label: String = "Out of Stock") : AttentionStatus
    data class LowStock(override val label: String = "Low Stock") : AttentionStatus
}

package com.iqbalfauzi.kitchenstock.presentation.shopping.model

enum class ShoppingBadgeType {
    Danger,   // Red dot, red text, soft red bg (e.g. Stok Habis di Kulkas)
    Warning,  // Amber dot, amber text, soft amber bg (e.g. Stok Menipis di Freezer)
    Neutral,  // Location/category tag + neutral container (e.g. Bumbu Dapur)
    Restock   // Green repeat icon + green text + soft green bg (e.g. Restock otomatis ke Pantry)
}

data class ShoppingItem(
    val id: String,
    val productId: String = "",
    val name: String,
    val quantity: String,
    val rawQuantity: Double = 1.0,
    val unit: String = "pcs",
    val isChecked: Boolean = false,
    val badgeText: String? = null,
    val badgeType: ShoppingBadgeType = ShoppingBadgeType.Neutral
)

data class ShoppingUiState(
    val quickAddInput: String = "",
    val pendingItems: List<ShoppingItem> = emptyList(),
    val boughtItems: List<ShoppingItem> = emptyList(),
    val isBoughtExpanded: Boolean = true,
    val isRefreshing: Boolean = false,
    val userMessage: String? = null
) {
    val pendingCount: Int get() = pendingItems.size
    val boughtCount: Int get() = boughtItems.size
    val totalCount: Int get() = pendingCount + boughtCount
    val isEmpty: Boolean get() = pendingItems.isEmpty() && boughtItems.isEmpty()
}

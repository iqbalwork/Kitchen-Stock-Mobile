package com.iqbalfauzi.kitchenstock.presentation.shopping.model

data class ShoppingUiState(
    val searchQuery: String = "",
    val groupedItems: Map<String, List<ShoppingItem>> = emptyMap(),
    val recentlyBought: List<String> = emptyList(),
    val isRefreshing: Boolean = false
)

data class ShoppingItem(
    val id: String,
    val name: String,
    val quantity: String,
    val isChecked: Boolean = false
)

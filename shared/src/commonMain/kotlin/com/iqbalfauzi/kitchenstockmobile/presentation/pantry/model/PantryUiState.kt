package com.iqbalfauzi.kitchenstockmobile.presentation.pantry.model

import androidx.compose.ui.graphics.vector.ImageVector

data class PantryUiState(
    val categories: List<String> = listOf("All", "Fridge", "Pantry", "Freezer", "Spices"),
    val selectedCategory: String = "All",
    val groupedItems: Map<String, List<PantryItem>> = emptyMap()
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

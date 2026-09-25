package com.iqbalfauzi.kitchenstock.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Destination : NavKey {
    // Bottom nav destinations
    @Serializable
    data object Pantry : Destination

    @Serializable
    data object Shopping : Destination

    @Serializable
    data object Settings : Destination

    // Secondary destinations
    @Serializable
    data class InventoryDetail(val id: String? = null) : Destination

    @Serializable
    data object AddShoppingItem : Destination
}

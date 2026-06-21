package com.iqbalfauzi.kitchenstockmobile.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Destination : NavKey {
    @Serializable
    data object Home : Destination

    @Serializable
    data object Pantry : Destination

    @Serializable
    data object Shopping : Destination

    @Serializable
    data class InventoryDetail(val id: String? = null) : Destination
}

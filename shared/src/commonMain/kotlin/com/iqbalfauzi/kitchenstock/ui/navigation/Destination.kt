package com.iqbalfauzi.kitchenstock.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Destination : NavKey {
    @Serializable
    data object Login : Destination

    @Serializable
    data object Home : Destination

    @Serializable
    data object Pantry : Destination

    @Serializable
    data object Shopping : Destination

    @Serializable
    data object Profile : Destination

    @Serializable
    data class InventoryDetail(val id: String? = null) : Destination

    @Serializable
    data object AddShoppingItem : Destination

    @Serializable
    data object SignUp : Destination

    @Serializable
    data object ForgotPassword : Destination
}

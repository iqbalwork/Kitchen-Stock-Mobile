package com.iqbalfauzi.kitchenstockmobile

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.iqbalfauzi.kitchenstockmobile.presentation.home.HomeScreen
import com.iqbalfauzi.kitchenstockmobile.presentation.pantry.PantryScreen
import com.iqbalfauzi.kitchenstockmobile.presentation.shopping.ShoppingScreen
import com.iqbalfauzi.kitchenstockmobile.ui.navigation.Destination
import com.iqbalfauzi.kitchenstockmobile.ui.theme.KitchenStockTheme
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Destination.Home::class, Destination.Home.serializer())
            subclass(Destination.Pantry::class, Destination.Pantry.serializer())
            subclass(Destination.Shopping::class, Destination.Shopping.serializer())
        }
    }
}

@Composable
@Preview
fun App() {
    KitchenStockTheme {
        val backStack = rememberNavBackStack(navConfig, Destination.Home)

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                NavigationBar(
                    windowInsets = WindowInsets(0, 0, 0, 0)
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = backStack.last() is Destination.Home,
                        onClick = {
                            if (backStack.last() !is Destination.Home) {
                                backStack.clear()
                                backStack.add(Destination.Home)
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Inventory, contentDescription = "Pantry") },
                        label = { Text("Pantry") },
                        selected = backStack.last() is Destination.Pantry,
                        onClick = {
                            if (backStack.last() !is Destination.Pantry) {
                                backStack.clear()
                                backStack.add(Destination.Pantry)
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Shopping List") },
                        label = { Text("Shopping List") },
                        selected = backStack.last() is Destination.Shopping,
                        onClick = {
                            if (backStack.last() !is Destination.Shopping) {
                                backStack.clear()
                                backStack.add(Destination.Shopping)
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                NavDisplay(
                    backStack = backStack,
                    entryProvider = entryProvider {
                        entry<Destination.Home> {
                            HomeScreen()
                        }
                        entry<Destination.Pantry> {
                            PantryScreen()
                        }
                        entry<Destination.Shopping> {
                            ShoppingScreen()
                        }
                    },
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    popTransitionSpec = { fadeIn() togetherWith fadeOut() }
                )
            }
        }
    }
}

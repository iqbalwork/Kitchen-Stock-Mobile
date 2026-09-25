package com.iqbalfauzi.kitchenstock

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.iqbalfauzi.kitchenstock.presentation.home.HomeScreen
import com.iqbalfauzi.kitchenstock.presentation.inventory_detail.InventoryDetailScreen
import com.iqbalfauzi.kitchenstock.presentation.pantry.PantryScreen
import com.iqbalfauzi.kitchenstock.presentation.profile.ProfileScreen
import com.iqbalfauzi.kitchenstock.presentation.shopping.AddShoppingItemScreen
import com.iqbalfauzi.kitchenstock.presentation.shopping.ShoppingScreen
import com.iqbalfauzi.kitchenstock.ui.navigation.Destination
import com.iqbalfauzi.kitchenstock.ui.theme.KitchenStockTheme
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Destination.Home::class, Destination.Home.serializer())
            subclass(Destination.Pantry::class, Destination.Pantry.serializer())
            subclass(Destination.Shopping::class, Destination.Shopping.serializer())
            subclass(Destination.Profile::class, Destination.Profile.serializer())
            subclass(Destination.InventoryDetail::class, Destination.InventoryDetail.serializer())
            subclass(Destination.AddShoppingItem::class, Destination.AddShoppingItem.serializer())
        }
    }
}

@Composable
@Preview
fun App() {
    KitchenStockTheme {
        val backStack = rememberNavBackStack(navConfig, Destination.Home)
        val lastDestination = backStack.lastOrNull()

        val showBottomBar = lastDestination != null &&
            lastDestination !is Destination.InventoryDetail

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(windowInsets = WindowInsets(0, 0, 0, 0)) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home") },
                            selected = lastDestination is Destination.Home,
                            onClick = {
                                if (lastDestination !is Destination.Home) {
                                    backStack.clear()
                                    backStack.add(Destination.Home)
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Inventory, contentDescription = "Pantry") },
                            label = { Text("Pantry") },
                            selected = lastDestination is Destination.Pantry,
                            onClick = {
                                if (lastDestination !is Destination.Pantry) {
                                    backStack.clear()
                                    backStack.add(Destination.Pantry)
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Shopping List") },
                            label = { Text("Shopping List") },
                            selected = lastDestination is Destination.Shopping,
                            onClick = {
                                if (lastDestination !is Destination.Shopping) {
                                    backStack.clear()
                                    backStack.add(Destination.Shopping)
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile") },
                            selected = lastDestination is Destination.Profile,
                            onClick = {
                                if (lastDestination !is Destination.Profile) {
                                    backStack.clear()
                                    backStack.add(Destination.Profile)
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                NavDisplay(
                    backStack = backStack,
                    entryProvider = entryProvider {
                        entry<Destination.Home> {
                            HomeScreen(onNavigateToDetail = { backStack.add(Destination.InventoryDetail(it)) })
                        }
                        entry<Destination.Pantry> {
                            PantryScreen(onNavigateToDetail = { backStack.add(Destination.InventoryDetail(it)) })
                        }
                        entry<Destination.Shopping> {
                            ShoppingScreen(
                                onNavigateToDetail = { backStack.add(Destination.InventoryDetail(it)) },
                                onNavigateToAddItem = { backStack.add(Destination.AddShoppingItem) }
                            )
                        }
                        entry<Destination.Profile> {
                            ProfileScreen()
                        }
                        entry<Destination.InventoryDetail> { key ->
                            InventoryDetailScreen(
                                id = key.id,
                                onBackClick = { backStack.removeLastOrNull() }
                            )
                        }
                        entry<Destination.AddShoppingItem> {
                            AddShoppingItemScreen(
                                onBackClick = { backStack.removeLastOrNull() }
                            )
                        }
                    },
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    popTransitionSpec = { fadeIn() togetherWith fadeOut() }
                )
            }
        }
    }
}

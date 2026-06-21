package com.iqbalfauzi.kitchenstockmobile

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.iqbalfauzi.kitchenstockmobile.domain.repository.AuthRepository
import com.iqbalfauzi.kitchenstockmobile.presentation.home.HomeScreen
import com.iqbalfauzi.kitchenstockmobile.presentation.inventory_detail.InventoryDetailScreen
import com.iqbalfauzi.kitchenstockmobile.presentation.pantry.PantryScreen
import com.iqbalfauzi.kitchenstockmobile.presentation.shopping.ShoppingScreen
import com.iqbalfauzi.kitchenstockmobile.ui.navigation.Destination
import com.iqbalfauzi.kitchenstockmobile.ui.theme.KitchenStockTheme
import io.github.aakira.napier.Napier
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.koinInject

private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Destination.Home::class, Destination.Home.serializer())
            subclass(Destination.Pantry::class, Destination.Pantry.serializer())
            subclass(Destination.Shopping::class, Destination.Shopping.serializer())
            subclass(Destination.InventoryDetail::class, Destination.InventoryDetail.serializer())
        }
    }
}

@Composable
@Preview
fun App() {
    Napier.d("App() Composable called")
    val authRepository = koinInject<AuthRepository>()
    var isReady by remember { mutableStateOf(false) }
    var hasCheckedAuth by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Napier.d("App LaunchedEffect started")
        try {
            val loggedIn = authRepository.isUserLoggedIn()
            Napier.d("App: isUserLoggedIn: $loggedIn")
            if (loggedIn) {
                isReady = true
            } else {
                Napier.d("App: signInAnonymously starting")
                authRepository.signInAnonymously()
                Napier.d("App: signInAnonymously finished")
                isReady = true
            }
        } catch (e: Exception) {
            Napier.e("App: Auth check/sign-in FAILED: ${e.message}", e)
        } finally {
            hasCheckedAuth = true
        }
    }

    KitchenStockTheme {
        Napier.d("App: inside KitchenStockTheme, isReady: $isReady, hasCheckedAuth: $hasCheckedAuth")
        if (!hasCheckedAuth || !isReady) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text("Initializing App...", color = Color.Black)
                }
            }
        } else {
            Napier.d("App: initializing backStack")
            val backStack = rememberNavBackStack(navConfig, Destination.Home)
            Napier.d("App: backStack initialized, last: ${backStack.lastOrNull()}")

            val showBottomBar = backStack.last() !is Destination.InventoryDetail

            Scaffold(
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    if (showBottomBar) {
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
                                ShoppingScreen(onNavigateToDetail = { backStack.add(Destination.InventoryDetail(it)) })
                            }
                            entry<Destination.InventoryDetail> { key ->
                                InventoryDetailScreen(
                                    id = key.id,
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
}

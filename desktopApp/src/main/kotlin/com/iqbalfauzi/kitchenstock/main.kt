package com.iqbalfauzi.kitchenstock

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.iqbalfauzi.kitchenstock.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "KitchenStock",
        ) {
            App()
        }
    }
}

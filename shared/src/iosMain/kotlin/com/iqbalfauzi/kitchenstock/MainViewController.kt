package com.iqbalfauzi.kitchenstock

import androidx.compose.ui.window.ComposeUIViewController
import com.iqbalfauzi.kitchenstock.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initKoin() }
) {
    App()
}

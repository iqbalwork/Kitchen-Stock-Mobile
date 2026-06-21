package com.iqbalfauzi.kitchenstockmobile

import androidx.compose.ui.window.ComposeUIViewController
import com.iqbalfauzi.kitchenstockmobile.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initKoin() }
) {
    App()
}

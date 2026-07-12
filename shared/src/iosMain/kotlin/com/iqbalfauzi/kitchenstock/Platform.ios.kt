package com.iqbalfauzi.kitchenstock

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual val ioDispatcher: CoroutineDispatcher = Dispatchers.Default

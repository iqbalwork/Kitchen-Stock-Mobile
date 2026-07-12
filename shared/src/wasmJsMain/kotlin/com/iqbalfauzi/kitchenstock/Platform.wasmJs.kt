package com.iqbalfauzi.kitchenstock

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

actual val ioDispatcher: CoroutineDispatcher = Dispatchers.Default

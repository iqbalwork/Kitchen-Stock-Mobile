package com.iqbalfauzi.kitchenstock

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

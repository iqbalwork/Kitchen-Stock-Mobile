package com.iqbalfauzi.kitchenstock

import android.os.Build
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

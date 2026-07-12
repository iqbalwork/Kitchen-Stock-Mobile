package com.iqbalfauzi.kitchenstock

import kotlinx.coroutines.CoroutineDispatcher

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect val ioDispatcher: CoroutineDispatcher

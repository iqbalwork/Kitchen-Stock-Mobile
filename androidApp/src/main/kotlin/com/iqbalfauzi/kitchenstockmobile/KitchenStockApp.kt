package com.iqbalfauzi.kitchenstockmobile

import android.app.Application
import com.iqbalfauzi.kitchenstockmobile.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class KitchenStockApp : Application() {
    override fun onCreate() {
        super.onCreate()
        println("DEBUG: KitchenStockApp onCreate started")
        initKoin {
            androidLogger()
            androidContext(this@KitchenStockApp)
        }
        println("DEBUG: KitchenStockApp onCreate finished")
    }
}

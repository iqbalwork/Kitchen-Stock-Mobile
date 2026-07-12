package com.iqbalfauzi.kitchenstock

import android.app.Application
import com.iqbalfauzi.kitchenstock.di.initKoin
import org.koin.android.ext.koin.androidContext

class KitchenStockApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@KitchenStockApp)
        }
    }
}

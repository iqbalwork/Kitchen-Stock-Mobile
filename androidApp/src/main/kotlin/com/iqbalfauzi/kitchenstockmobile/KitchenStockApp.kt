package com.iqbalfauzi.kitchenstockmobile

import android.app.Application
import com.iqbalfauzi.kitchenstockmobile.di.initKoin
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class KitchenStockApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@KitchenStockApp)
        }
        Napier.d("KitchenStockApp onCreate finished")
    }
}

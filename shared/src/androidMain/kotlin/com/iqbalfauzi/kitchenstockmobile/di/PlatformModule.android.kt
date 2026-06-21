package com.iqbalfauzi.kitchenstockmobile.di

import android.content.Context
import com.iqbalfauzi.kitchenstockmobile.db.DbDriverFactory
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DbDriverFactory(get()) }
    single<Settings> {
        val sharedPrefs = get<Context>().getSharedPreferences("kitchen_stock_prefs", Context.MODE_PRIVATE)
        SharedPreferencesSettings(sharedPrefs)
    }
}

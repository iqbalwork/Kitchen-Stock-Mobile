package com.iqbalfauzi.kitchenstock.di

import android.content.Context
import com.iqbalfauzi.kitchenstock.db.DbDriverFactory
import com.iqbalfauzi.kitchenstock.db.KitchenDatabase
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import app.cash.sqldelight.db.SqlDriver
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DbDriverFactory(get()) }
    single<SqlDriver> { get<DbDriverFactory>().createDriver() }
    single<Settings> {
        val sharedPrefs = get<Context>().getSharedPreferences("kitchen_stock_prefs", Context.MODE_PRIVATE)
        SharedPreferencesSettings(sharedPrefs)
    }
}

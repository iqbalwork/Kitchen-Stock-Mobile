package com.iqbalfauzi.kitchenstock.di

import com.iqbalfauzi.kitchenstock.db.DbDriverFactory
import com.russhwolf.settings.Settings
import com.russhwolf.settings.PreferencesSettings
import app.cash.sqldelight.db.SqlDriver
import org.koin.core.module.Module
import org.koin.dsl.module
import java.util.prefs.Preferences

actual val platformModule: Module = module {
    single { DbDriverFactory() }
    single<SqlDriver> { get<DbDriverFactory>().createDriver() }
    single<Settings> {
        val prefs = Preferences.userRoot().node("kitchen_stock_prefs")
        PreferencesSettings(prefs)
    }
}

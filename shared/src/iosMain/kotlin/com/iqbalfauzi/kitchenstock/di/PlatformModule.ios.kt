package com.iqbalfauzi.kitchenstock.di

import com.iqbalfauzi.kitchenstock.db.DbDriverFactory
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import app.cash.sqldelight.db.SqlDriver
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual val platformModule: Module = module {
    single { DbDriverFactory() }
    single<SqlDriver> { get<DbDriverFactory>().createDriver() }
    single<Settings> {
        val userDefaults = NSUserDefaults.standardUserDefaults
        NSUserDefaultsSettings(userDefaults)
    }
}

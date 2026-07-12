package com.iqbalfauzi.kitchenstock.di

import com.iqbalfauzi.kitchenstock.db.DbDriverFactory
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import app.cash.sqldelight.db.SqlDriver
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DbDriverFactory() }
    single<SqlDriver> { get<DbDriverFactory>().createDriver() }
    single<Settings> {
        StorageSettings()
    }
}

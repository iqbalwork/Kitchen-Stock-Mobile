package com.iqbalfauzi.kitchenstockmobile.di

import com.iqbalfauzi.kitchenstockmobile.db.DbDriverFactory
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual val platformModule: Module = module {
    single { DbDriverFactory() }
    single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
}

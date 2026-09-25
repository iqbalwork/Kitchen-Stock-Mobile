package com.iqbalfauzi.kitchenstock.di

import com.iqbalfauzi.kitchenstock.data.repository.InventoryRepositoryImpl
import com.iqbalfauzi.kitchenstock.data.repository.ShoppingRepositoryImpl
import com.iqbalfauzi.kitchenstock.db.KitchenDatabase
import com.iqbalfauzi.kitchenstock.domain.repository.InventoryRepository
import com.iqbalfauzi.kitchenstock.domain.repository.ShoppingRepository
import com.russhwolf.settings.Settings
import org.koin.dsl.module

val dataModule = module {
    single<Settings> { Settings() }

    single {
        val driver = get<app.cash.sqldelight.db.SqlDriver>()
        KitchenDatabase(driver)
    }

    single<InventoryRepository> {
        InventoryRepositoryImpl(get())
    }

    single<ShoppingRepository> {
        ShoppingRepositoryImpl(get())
    }
}

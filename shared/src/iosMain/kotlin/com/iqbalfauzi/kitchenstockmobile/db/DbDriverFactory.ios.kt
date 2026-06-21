package com.iqbalfauzi.kitchenstockmobile.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DbDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(KitchenDatabase.Schema, "KitchenDatabase.db")
    }
}

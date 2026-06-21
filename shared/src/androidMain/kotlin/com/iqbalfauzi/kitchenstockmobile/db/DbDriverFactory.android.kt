package com.iqbalfauzi.kitchenstockmobile.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DbDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(KitchenDatabase.Schema, context, "KitchenDatabase.db")
    }
}

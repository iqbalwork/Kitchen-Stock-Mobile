package com.iqbalfauzi.kitchenstock.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DbDriverFactory {
    actual fun createDriver(): SqlDriver {
        val databaseFile = File(System.getProperty("user.home"), "KitchenDatabase.db")
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")
        if (!databaseFile.exists()) {
            KitchenDatabase.Schema.create(driver)
        }
        return driver
    }
}

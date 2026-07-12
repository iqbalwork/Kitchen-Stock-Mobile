package com.iqbalfauzi.kitchenstock.db

import app.cash.sqldelight.db.SqlDriver

expect class DbDriverFactory {
    fun createDriver(): SqlDriver
}

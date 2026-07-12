package com.iqbalfauzi.kitchenstock.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

actual class DbDriverFactory {
    actual fun createDriver(): SqlDriver {
        return WebWorkerDriver(Worker("sqldelight-worker.js"))
    }
}

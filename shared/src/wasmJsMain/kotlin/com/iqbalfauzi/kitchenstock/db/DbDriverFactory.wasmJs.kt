package com.iqbalfauzi.kitchenstock.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

actual class DbDriverFactory {
    actual fun createDriver(): SqlDriver {
        // This requires a worker script to be present.
        // For now, we might use a mock or in-memory if possible, 
        // but WebWorkerDriver is what's expected by the library.
        // In a real app, you'd provide the path to the worker.
        return WebWorkerDriver(Worker("sqldelight-worker.js"))
    }
}

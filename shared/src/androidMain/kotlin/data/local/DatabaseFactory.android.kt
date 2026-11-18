package com.example.mockup_mobapp.data.local

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.cash.sqldelight.db.SqlDriver

actual fun createDatabaseDriver(platformContext: Any): SqlDriver {
    val context = platformContext as Context

    return AndroidSqliteDriver(
        schema = AppDatabase.Schema,
        context = context,
        name = "lost_found.db"
    )
}
package com.example.mockup_mobapp.data.local

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.sqldelight.db.SqlDriver
import com.example.mockup_mobapp.data.local.AppDatabase

actual fun createDatabaseDriver(platformContext: Any): SqlDriver {
    val databasePath = platformContext as String

    return JdbcSqliteDriver(
        url = "jdbc:sqlite:$databasePath",
        schema = AppDatabase.Schema
    )
}
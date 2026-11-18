package com.example.mockup_mobapp.data.local

import app.cash.sqldelight.db.SqlDriver
import com.example.mockup_mobapp.data.local.AppDatabase

expect fun createDatabaseDriver(platformContext: Any): SqlDriver

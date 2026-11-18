package com.example.mockup_mobapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

import com.example.mockup_mobapp.data.local.AppDatabase
import com.example.mockup_mobapp.data.local.createDatabaseDriver
import java.io.File

fun main() = application {
    val databaseDirectory = File(
        System.getProperty("user.home"),
        ".mockup_mobapp"
    )

    check(databaseDirectory.mkdirs() || databaseDirectory.isDirectory) {
        "Could not create database directory."
    }

    val database = AppDatabase(
        createDatabaseDriver(
            File(databaseDirectory, "lost_found.db").absolutePath
        )
    )
    Window(
        onCloseRequest = ::exitApplication,
        title = "mockup_mobapp",
    ) {
        App(database)
    }
}
package com.example.mockup_mobapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "mockup_mobapp",
    ) {
        App()
    }
}
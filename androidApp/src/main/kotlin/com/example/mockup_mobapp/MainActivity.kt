
package com.example.mockup_mobapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import com.example.mockup_mobapp.data.local.AppDatabase
import com.example.mockup_mobapp.data.local.createDatabaseDriver

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val database = AppDatabase(
            createDatabaseDriver(this)
        )

        setContent {
            App(database)
        }
    }
}


package com.example.mockup_mobapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.mockup_mobapp.ui.screens.FindItemScreen
import com.example.mockup_mobapp.ui.screens.HomeScreen

@Composable
@Preview
fun App() {

    var showFindItem by remember {
        mutableStateOf(false)
    }

    MaterialTheme {

        if (showFindItem) {

            FindItemScreen(
                onBack = {
                    showFindItem = false
                }
            )

        } else {

            HomeScreen(
                onFindItem = {
                    showFindItem = true
                }
            )
        }
    }
}

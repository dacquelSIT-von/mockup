
package com.example.mockup_mobapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

import com.example.mockup_mobapp.ui.screens.FindItemScreen
import com.example.mockup_mobapp.ui.screens.HomeScreen
import com.example.mockup_mobapp.ui.screens.ReportItemScreen
import com.example.mockup_mobapp.ui.screens.ItemDetailsScreen
import com.example.mockup_mobapp.data.model.FoundItem
import com.example.mockup_mobapp.ui.screens.MyAccountScreen
import com.example.mockup_mobapp.ui.screens.MyActivityScreen
import com.example.mockup_mobapp.ui.screens.ReportProblemScreen

@Composable
@Preview
fun App() {
    var currentScreen by remember {
        mutableStateOf(Screen.HOME)
    }

    var selectedItem by remember {
        mutableStateOf<FoundItem?>(null)
    }

    MaterialTheme {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val isDesktop = maxWidth >= 600.dp

            if (isDesktop) {
                Row(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(
                        currentScreen = currentScreen,
                        onNavigate = { currentScreen = it },
                        isDesktop = true
                    )

                    ScreenContent(
                        currentScreen = currentScreen,
                        onNavigate = { currentScreen = it },
                        modifier = Modifier.weight(1f),
                        selectedItem = selectedItem,
                        onItemSelected = { item ->
                            selectedItem = item
                        }
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    ScreenContent(
                        currentScreen = currentScreen,
                        onNavigate = { currentScreen = it },
                        modifier = Modifier.weight(1f),
                        selectedItem = selectedItem,
                        onItemSelected = { item ->
                            selectedItem = item
                        }
                    )

                    AppNavigation(
                        currentScreen = currentScreen,
                        onNavigate = { currentScreen = it },
                        isDesktop = false
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenContent(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier,
    selectedItem: FoundItem?,
    onItemSelected: (FoundItem) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (currentScreen) {
            Screen.HOME -> HomeScreen(
                onFindItem = {
                    onNavigate(Screen.FIND_ITEM)
                }
            )

            Screen.FIND_ITEM -> FindItemScreen(
                onBack = { onNavigate(Screen.HOME) },
                onItemSelected = { item ->
                    onItemSelected(item)
                    onNavigate(Screen.ITEM_DETAILS)
                }
            )

            Screen.REPORT_ITEM ->
                ReportItemScreen()

            Screen.ITEM_DETAILS -> {
                selectedItem?.let { item ->
                    ItemDetailsScreen(
                        item = item,
                        onReportProblem = {
                            onNavigate(Screen.REPORT_PROBLEM)
                        }
                    )
                }
            }

            Screen.REPORT_PROBLEM -> {
                selectedItem?.let { item ->
                    ReportProblemScreen(
                        item = item,
                        onBack = {
                            onNavigate(Screen.ITEM_DETAILS)
                        }
                    )
                }
            }

            Screen.MY_ACTIVITY ->
                MyActivityScreen()

            Screen.MY_ACCOUNT ->
                MyAccountScreen()

        }
    }
}

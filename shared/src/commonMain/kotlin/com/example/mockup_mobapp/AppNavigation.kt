
package com.example.mockup_mobapp

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AppNavigation(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    isDesktop: Boolean
) {
    val navigationItems = listOf(
        Triple(Screen.HOME, "Home", "⌂"),
        Triple(Screen.FIND_ITEM, "Find Item", "⌕"),
        Triple(Screen.REPORT_ITEM, "Report Item", "+"),
        Triple(Screen.MY_ACTIVITY, "My Activity", "▤"),
        Triple(Screen.MY_ACCOUNT, "My Account", "●")
    )

    if (isDesktop) {
        NavigationRail {
            navigationItems.forEach { (screen, label, symbol) ->
                NavigationRailItem(
                    selected = currentScreen == screen,
                    onClick = { onNavigate(screen) },
                    icon = { Text(symbol) },
                    label = { Text(label) }
                )
            }
        }
    } else {
        NavigationBar {
            navigationItems.forEach { (screen, label, symbol) ->
                NavigationBarItem(
                    selected = currentScreen == screen,
                    onClick = { onNavigate(screen) },
                    icon = { Text(symbol) },
                    label = { Text(label) }
                )
            }
        }
    }
}

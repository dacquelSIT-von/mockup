
package com.example.mockup_mobapp

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mockup_mobapp.data.model.UserRole

internal fun navigationScreensForRole(role: UserRole): List<Screen> = buildList {
    add(Screen.HOME)
    add(Screen.FIND_ITEM)
    if (role != UserRole.STAFF) add(Screen.REPORT_ITEM)
    add(Screen.MY_ACTIVITY)
    add(Screen.MY_ACCOUNT)
    if (role == UserRole.STAFF) add(Screen.STAFF_CLAIMS)
}

@Composable
fun AppNavigation(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    isDesktop: Boolean,
    role: UserRole
) {
    val navigationItems = buildList {
        add(Triple(Screen.HOME, "Home", "⌂"))
        add(Triple(Screen.FIND_ITEM, "Find Item", "⌕"))
        if (role != UserRole.STAFF) {
            add(Triple(Screen.REPORT_ITEM, "Report Found Item", "+"))
        }
        add(
            Triple(
                Screen.MY_ACTIVITY,
                if (role == UserRole.STAFF) "Reports" else "My Activity",
                "▤"
            )
        )
        add(Triple(Screen.MY_ACCOUNT, "My Account", "●"))
        if (role == UserRole.STAFF) {
            add(Triple(Screen.STAFF_CLAIMS, "Claims to Review", "✓"))
        }
    }

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

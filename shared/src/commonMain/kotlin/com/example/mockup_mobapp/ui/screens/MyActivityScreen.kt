
package com.example.mockup_mobapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MyActivityScreen() {

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    val tabs = listOf("My Reports", "My Claims")

    val reports = listOf(
        "Black Wallet" to "Available",
        "Blue Water Bottle" to "Returned"
    )

    val claims = listOf(
        "Student ID" to "Pending",
        "Umbrella" to "Rejected"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "My Activity",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Track your reported items and ownership claims.",
            style = MaterialTheme.typography.bodyMedium
        )

        TabRow(
            selectedTabIndex = selectedTab
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        if (selectedTab == 0) {

            reports.forEach { (itemName, status) ->
                ActivityCard(
                    itemName = itemName,
                    status = status
                )
            }

        } else {

            claims.forEach { (itemName, status) ->
                ActivityCard(
                    itemName = itemName,
                    status = status
                )
            }
        }
    }
}

@Composable
fun ActivityCard(
    itemName: String,
    status: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = itemName,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Status: $status",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


package com.example.mockup_mobapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.mockup_mobapp.domain.repository.FoundItemRepository
import com.example.mockup_mobapp.data.model.FoundItem
import com.example.mockup_mobapp.data.model.Claim
@Composable
fun MyActivityScreen(
    repository: FoundItemRepository
) {

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    var reports by remember {
        mutableStateOf<List<FoundItem>>(emptyList())
    }

    var claims by remember {
        mutableStateOf<List<Claim>>(emptyList())
    }

    LaunchedEffect(repository) {
        reports = repository.getFoundItems()
        claims = repository.getClaims()
    }

    val tabs = listOf("My Reports", "My Claims")

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
            reports.forEach { item ->
                ActivityCard(
                    itemName = item.itemName,
                    status = item.status.name
                )
            }
        } else {
            claims.forEach { claim ->
                ActivityCard(
                    itemName = reports.find { it.id == claim.itemId }?.itemName
                        ?: "Unknown Item",
                    status = claim.status.name
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

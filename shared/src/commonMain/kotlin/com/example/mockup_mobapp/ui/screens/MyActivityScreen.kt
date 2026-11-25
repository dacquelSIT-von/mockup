
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
import com.example.mockup_mobapp.data.model.UserRole
import kotlinx.coroutines.CancellationException
@Composable
fun MyActivityScreen(
    repository: FoundItemRepository,
    currentUserId: String,
    userRole: UserRole
) {

    var selectedTab by remember(userRole) {
        mutableIntStateOf(0)
    }

    var reports by remember {
        mutableStateOf<List<FoundItem>>(emptyList())
    }

    var claims by remember {
        mutableStateOf<List<Claim>>(emptyList())
    }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(repository, currentUserId, userRole, refreshKey) {
        isLoading = true
        errorMessage = null

        try {
            val loadedReports = repository.getFoundItems()
            val loadedClaims = if (userRole == UserRole.STAFF) {
                emptyList()
            } else {
                repository.getClaims()
            }
            reports = loadedReports
            claims = loadedClaims
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Exception) {
            errorMessage = if (userRole == UserRole.STAFF) {
                "Unable to load reports. Check your connection and try again."
            } else {
                "Unable to load your activity. Check your connection and try again."
            }
        } finally {
            isLoading = false
        }
    }

    val visibleReports = if (userRole == UserRole.STAFF) {
        reports
    } else {
        reports.filter { it.userId == currentUserId }
    }
    val visibleClaims = claims.filter { it.claimantId == currentUserId }

    val tabs = if (userRole == UserRole.STAFF) {
        listOf("Reports")
    } else {
        listOf("My Reports", "My Claims")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = if (userRole == UserRole.STAFF) "Reports" else "My Activity",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = if (userRole == UserRole.STAFF) {
                "View item reports submitted by all users."
            } else {
                "Track your reported items and ownership claims."
            },
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

        when {
            isLoading -> {
                CircularProgressIndicator()
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )
                Button(onClick = { refreshKey++ }) {
                    Text("Retry")
                }
            }

            selectedTab == 0 -> {
                if (visibleReports.isEmpty()) {
                    Text(
                        if (userRole == UserRole.STAFF) {
                            "No reports are available."
                        } else {
                            "You haven't submitted any reports yet."
                        }
                    )
                } else {
                    visibleReports.forEach { item ->
                        ActivityCard(
                            itemName = item.itemName,
                            status = item.status.name
                        )
                    }
                }
            }

            visibleClaims.isEmpty() -> {
                Text("You don't have any claims yet.")
            }

            else -> {
                visibleClaims.forEach { claim ->
                    ActivityCard(
                        itemName = reports.find { it.id == claim.itemId }?.itemName
                            ?: "Unknown Item",
                        status = claim.status.name
                    )
                }
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


package com.example.mockup_mobapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException

import com.example.mockup_mobapp.data.model.FoundItem
import com.example.mockup_mobapp.data.model.ItemStatus
import com.example.mockup_mobapp.domain.repository.FoundItemRepository

@Composable
fun FindItemScreen(
    repository: FoundItemRepository,
    onBack: () -> Unit = {},
    onItemSelected: (FoundItem) -> Unit = {},
) {
    var items by remember {
        mutableStateOf<List<FoundItem>>(emptyList())
    }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableStateOf(0) }

    // Fetch items whenever the repository or refresh key changes.
    LaunchedEffect(repository, refreshKey) {
        isLoading = true
        errorMessage = null

        try {
            items = repository.getFoundItems()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            errorMessage = "Unable to load items. Please check your connection and try again."
        } finally {
            isLoading = false
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All Categories") }
    var selectedLocation by remember { mutableStateOf("All Locations") }
    var selectedTab by remember { mutableStateOf(0) }

    val categories = listOf(
        "All Categories",
        "Electronics",
        "Bags",
        "IDs",
        "Clothing",
        "Accessories",
        "Other"
    )

    val locations = listOf(
        "All Locations",
        "Library",
        "Computer Laboratory",
        "Cafeteria",
        "Main Building"
    )

    val filteredItems = items.filter { item ->

        val matchesSearch =
            item.itemName.contains(searchQuery, ignoreCase = true) ||
            item.description.contains(searchQuery, ignoreCase = true)

        val matchesCategory =
            selectedCategory == "All Categories" ||
            item.category == selectedCategory

        val matchesLocation =
            selectedLocation == "All Locations" ||
            item.location == selectedLocation

        val selectedStatus = when (selectedTab) {
            0 -> ItemStatus.AVAILABLE
            1 -> ItemStatus.CLAIM_PENDING
            else -> ItemStatus.RETURNED
        }
        val matchesStatus = item.status == selectedStatus

        matchesStatus && matchesSearch && matchesCategory && matchesLocation
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("← Back to Home")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Find an Item",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Search for an item that you may have lost.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search item name or keyword") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Filter Items",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        FilterDropdown(
            label = "Category",
            options = categories,
            selectedOption = selectedCategory,
            onOptionSelected = { selectedCategory = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        FilterDropdown(
            label = "Location",
            options = locations,
            selectedOption = selectedLocation,
            onOptionSelected = { selectedLocation = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {
                searchQuery = ""
                selectedCategory = "All Categories"
                selectedLocation = "All Locations"
            }
        ) {
            Text("Reset Filters")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Available") }
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Claim Pending") }
            )

            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Returned") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Loading items...")
                    }
                }
            }

            errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Something went wrong",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Button(
                        onClick = { refreshKey++ }
                    ) {
                        Text("Try Again")
                    }
                }
            }

            else -> {
                Text(
                    text = "Items (${filteredItems.size})",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (filteredItems.isEmpty()) {
                    Text(
                        text = "No matching items found.",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "Try another keyword or reset your filters.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    filteredItems.forEach { item ->
                        Card(
                            onClick = {
                                onItemSelected(item)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = item.itemName,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text("Category: ${item.category}")
                                Text("Location: ${item.location}")
                                Text("Date Found: ${item.dateFound}")

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("$label: $selectedOption")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

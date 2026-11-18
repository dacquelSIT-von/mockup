
package com.example.mockup_mobapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class FoundItem(
    val name: String,
    val category: String,
    val location: String,
    val date: String,
    val description: String
)

@Composable
fun FindItemScreen(
    onBack: () -> Unit = {},
    onItemSelected: (FoundItem) -> Unit = {},
) {
    val sampleItems = remember {
        listOf(
            FoundItem(
                "Black Wallet",
                "Personal Belongings",
                "Library",
                "November 15, 2026",
                "A black leather wallet found near the entrance."
            ),
            FoundItem(
                "Blue Water Bottle",
                "Accessories",
                "Computer Laboratory",
                "November 14, 2026",
                "A blue reusable water bottle."
            ),
            FoundItem(
                "Student ID",
                "Documents",
                "Cafeteria",
                "November 13, 2026",
                "A student identification card."
            ),
            FoundItem(
                "Umbrella",
                "Accessories",
                "Main Building",
                "November 12, 2026",
                "A folded black umbrella."
            )
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All Categories") }
    var selectedLocation by remember { mutableStateOf("All Locations") }

    val categories = listOf(
        "All Categories",
        "Personal Belongings",
        "Accessories",
        "Documents"
    )

    val locations = listOf(
        "All Locations",
        "Library",
        "Computer Laboratory",
        "Cafeteria",
        "Main Building"
    )

    val filteredItems = sampleItems.filter { item ->
        val matchesSearch =
            item.name.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true)

        val matchesCategory =
            selectedCategory == "All Categories" ||
                    item.category == selectedCategory

        val matchesLocation =
            selectedLocation == "All Locations" ||
                    item.location == selectedLocation

        matchesSearch && matchesCategory && matchesLocation
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

        Text(
            text = "Found Items (${filteredItems.size})",
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
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text("Category: ${item.category}")
                        Text("Location: ${item.location}")
                        Text("Date Found: ${item.date}")

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


package com.example.mockup_mobapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mockup_mobapp.data.model.FoundItem

@Composable
fun FindItemScreen(
    onBack: () -> Unit
) {
    var searchQuery by remember {
        mutableStateOf("")
    }

    val foundItems = listOf(
        FoundItem(
            id = "1",
            itemName = "Blue Water Bottle",
            category = "Personal Belongings",
            description = "Blue water bottle",
            location = "SIT Building",
            dateFound = "November 7, 2026"
        ),
        FoundItem(
            id = "2",
            itemName = "Black Wallet",
            category = "Accessories",
            description = "Black leather wallet",
            location = "SIT Laboratory",
            dateFound = "November 6, 2026"
        ),
        FoundItem(
            id = "3",
            itemName = "USB Flash Drive",
            category = "Gadgets",
            description = "Black USB flash drive",
            location = "SIT Classroom",
            dateFound = "November 5, 2026"
        )
    )

    val filteredItems = foundItems.filter {
        it.itemName.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Find an Item",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = {
                Text("Search for an item")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text(
            text = "Recently Found Items",
            style = MaterialTheme.typography.titleLarge
        )

        if (filteredItems.isEmpty()) {

            Text("No matching items found.")

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredItems) { item ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = item.itemName,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text("Category: ${item.category}")
                            Text("Location: ${item.location}")
                            Text("Date Found: ${item.dateFound}")
                            Text("Status: ${item.status}")
                        }
                    }
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Home")
        }
    }
}


package com.example.mockup_mobapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onFindItem: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Lost and Found",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "SIT Department",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome!",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Find lost belongings or report an item you found."
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onFindItem,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Find an Item")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Report a Found Item")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Recently Found Items",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Blue Water Bottle",
                    style = MaterialTheme.typography.titleMedium
                )

                Text("Category: Personal Belongings")
                Text("Location: SIT Building")
                Text("Status: Available")
            }
        }
    }
}

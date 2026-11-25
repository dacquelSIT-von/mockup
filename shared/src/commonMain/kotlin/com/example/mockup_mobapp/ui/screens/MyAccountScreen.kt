
package com.example.mockup_mobapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mockup_mobapp.data.model.UserProfile

@Composable
fun MyAccountScreen(
    profile: UserProfile,
    onLogout: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = "My Account",
            style = MaterialTheme.typography.headlineMedium
        )

        // Profile Information
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "${profile.role.name.lowercase().replaceFirstChar { it.uppercase() }} Profile",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = profile.fullName,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Account ID: ${profile.id}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Account Options
        Text(
            text = "Account Settings",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit Profile")
        }

        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Privacy and Safety")
        }

        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Help and Guidelines")
        }

        Text(
            text = "Lost & Found SIT Dept.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Logout")
        }
    }
}

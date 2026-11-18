
package com.example.mockup_mobapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.mockup_mobapp.data.model.FoundItem

@Composable
fun ReportProblemScreen(
    item: FoundItem,
    onBack: () -> Unit = {}
) {

    var selectedReason by remember {
        mutableStateOf("False ownership claim")
    }

    var explanation by remember {
        mutableStateOf("")
    }

    var reportMessage by remember {
        mutableStateOf("")
    }

    val reportTypes = listOf(
        "False ownership claim",
        "Wrong or fake item information"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Report a Problem",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Item: ${item.itemName}",
            style = MaterialTheme.typography.titleMedium
        )

        Text("What do you want to report?")

        reportTypes.forEach { reason ->

            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedReason == reason,
                    onClick = {
                        selectedReason = reason
                    }
                )

                Text(reason)
            }
        }

        OutlinedTextField(
            value = explanation,
            onValueChange = { explanation = it },
            label = { Text("What happened?") },
            placeholder = {
                Text("Explain the problem here.")
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        Text(
            text = "Your report will be reviewed by an administrator.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
            onClick = {
                if (explanation.isNotBlank()) {
                    reportMessage =
                        "Your report has been submitted. Backend integration is pending."
                    explanation = ""
                } else {
                    reportMessage =
                        "Please explain the problem before submitting."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Report")
        }

        if (reportMessage.isNotEmpty()) {
            Text(
                text = reportMessage,
                color = MaterialTheme.colorScheme.primary
            )
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go Back")
        }
    }
}

package com.example.mockup_mobapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.mockup_mobapp.data.model.FoundItem
import com.example.mockup_mobapp.domain.repository.FoundItemRepository
import com.example.mockup_mobapp.data.model.Claim
import kotlinx.coroutines.launch
@Composable
fun ItemDetailsScreen(
item: FoundItem,
repository: FoundItemRepository,
onReportProblem: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var isSubmitting by remember { mutableStateOf(false) }

    var showClaimForm by remember {
        mutableStateOf(false)
    }

    var claimReason by remember {
        mutableStateOf("")
    }

    var identifyingDetails by remember {
        mutableStateOf("")
    }

    var claimMessage by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Item Details",
            style = MaterialTheme.typography.headlineMedium
        )

        // Item Photo
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Item Photo")
            }
        }

        // Item Information
        Text(
            text = item.itemName,
            style = MaterialTheme.typography.headlineSmall
        )

        AssistChip(
            onClick = {},
            label = { Text(item.category) }
        )

        HorizontalDivider()

        Text(
            text = "Found At",
            style = MaterialTheme.typography.titleMedium
        )
        Text(item.location)

        Text(
            text = "Date Found",
            style = MaterialTheme.typography.titleMedium
        )
        Text(item.dateFound)

        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium
        )
        Text(item.description)

        HorizontalDivider()


// Claim Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Is This Your Item?",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "Submit a claim if you believe this item belongs to you. " +
                            "You may be asked to provide identifying details to verify ownership."
                )

                Button(
                    onClick = {
                        showClaimForm = !showClaimForm
                        claimMessage = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("I Think This Is Mine")
                }

                if (showClaimForm) {

                    OutlinedTextField(
                        value = claimReason,
                        onValueChange = { claimReason = it },
                        label = { Text("Why are you claiming this item?") },
                        placeholder = { Text("Explain why you believe it is yours.") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    OutlinedTextField(
                        value = identifyingDetails,
                        onValueChange = { identifyingDetails = it },
                        label = { Text("Identifying Details") },
                        placeholder = {
                            Text("Describe unique details only the owner would know.")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Button(
                        onClick = {
                            if (
                                claimReason.isNotBlank() &&
                                identifyingDetails.isNotBlank() &&
                                !isSubmitting
                            ) {
                                coroutineScope.launch {
                                    isSubmitting = true

                                    try {
                                        val newClaim = Claim(
                                            id = "claim-${kotlin.random.Random.nextLong()}",
                                            itemId = item.id,
                                            claimantId = null,
                                            claimReason = claimReason,
                                            identifyingDetails = identifyingDetails,
                                            createdAt = kotlin.time.Clock.System.now().toString()
                                        )

                                        repository.addClaim(newClaim)

                                        claimMessage = "Your claim has been submitted successfully!"
                                        showClaimForm = false
                                        claimReason = ""
                                        identifyingDetails = ""

                                    } catch (exception: Exception) {
                                    exception.printStackTrace()
                                    claimMessage = "Claim error: ${exception.message}"
                                    } finally {
                                        isSubmitting = false
                                    }
                                }
                            } else {
                                claimMessage = "Please complete both fields before submitting."
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isSubmitting) "Submitting..." else "Submit Claim")
                    }
                }

                if (claimMessage.isNotEmpty()) {
                    Text(
                        text = claimMessage,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        OutlinedButton(
            onClick = onReportProblem,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Report a Problem")
        }
    }
}
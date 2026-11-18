package com.example.mockup_mobapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mockup_mobapp.domain.repository.FoundItemRepository
import com.example.mockup_mobapp.data.model.Claim
import com.example.mockup_mobapp.data.model.FoundItem
import com.example.mockup_mobapp.data.model.ClaimStatus
import kotlinx.coroutines.launch
import com.example.mockup_mobapp.data.model.ItemStatus

@Composable
fun StaffClaimScreen(
    repository: FoundItemRepository
) {
    val coroutineScope = rememberCoroutineScope()

    var claims by remember {
        mutableStateOf<List<Claim>>(emptyList())
    }

    var items by remember {
        mutableStateOf<List<FoundItem>>(emptyList())
    }

    LaunchedEffect(repository) {
        claims = repository.getClaims()
        items = repository.getFoundItems()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Manage Claims",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Review student ownership claims."
        )
        Text(
            text = "Pending Claims",
            style = MaterialTheme.typography.titleLarge
        )

        if (claims.isEmpty()) {
            Text("No claims have been submitted yet.")
        } else {
            claims.filter { it.status == ClaimStatus.PENDING }
                .forEach { claim ->

                    val itemName = items.find {
                        it.id == claim.itemId
                    }?.itemName ?: "Unknown Item"

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

                            Text("Reason: ${claim.claimReason}")

                            Text("Identifying Details: ${claim.identifyingDetails}")

                            Text("Submitted: ${claim.createdAt}")

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            repository.updateClaimStatus(
                                                claimId = claim.id,
                                                status = ClaimStatus.APPROVED
                                            )

                                            repository.rejectOtherPendingClaims(
                                                itemId = claim.itemId,
                                                approvedClaimId = claim.id
                                            )

                                            repository.updateItemStatus(
                                                itemId = claim.itemId,
                                                status = ItemStatus.RETURNED
                                            )
                                            claims = repository.getClaims()
                                        }
                                    }
                                ) {
                                    Text("Approve")
                                }

                                OutlinedButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            repository.updateClaimStatus(
                                                claimId = claim.id,
                                                status = ClaimStatus.REJECTED
                                            )

                                            claims = repository.getClaims()
                                        }
                                    }
                                ) {
                                    Text("Reject")
                                }
                            }
                        }
                    }
                }
        }
    }
}
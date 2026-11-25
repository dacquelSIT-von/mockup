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
import com.example.mockup_mobapp.data.model.ItemStatus
import kotlinx.coroutines.launch

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
    var actionInProgress by remember { mutableStateOf<String?>(null) }
    var actionMessage by remember { mutableStateOf("") }

    LaunchedEffect(repository) {
        runCatching {
            claims = repository.getClaims()
            items = repository.getFoundItems()
        }.onFailure { error ->
            actionMessage = error.message ?: "Could not load claims."
        }
    }

    fun reviewClaim(claim: Claim, approve: Boolean) {
        if (actionInProgress != null) return
        coroutineScope.launch {
            actionInProgress = claim.id
            actionMessage = ""
            var currentStep = "updating claim"
            var claimWasApproved = false
            try {
                repository.updateClaimStatus(
                    claimId = claim.id,
                    status = if (approve) ClaimStatus.APPROVED else ClaimStatus.REJECTED
                )
                if (approve) {
                    claimWasApproved = true
                    currentStep = "updating item status"
                    repository.updateItemStatus(
                        itemId = claim.itemId,
                        status = ItemStatus.RETURNED
                    )
                    currentStep = "closing other pending claims"
                    repository.rejectOtherPendingClaims(
                        itemId = claim.itemId,
                        approvedClaimId = claim.id
                    )
                }
                claims = repository.getClaims()
                items = repository.getFoundItems()
                actionMessage = if (approve) "Claim approved." else "Claim rejected."
            } catch (error: Exception) {
                if (approve && claimWasApproved && currentStep == "updating item status") {
                    runCatching {
                        repository.updateClaimStatus(claim.id, ClaimStatus.PENDING)
                    }
                }
                actionMessage = "Could not complete $currentStep: ${error.message ?: "Unknown error"}"
                runCatching { claims = repository.getClaims() }
            } finally {
                actionInProgress = null
            }
        }
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
        if (actionMessage.isNotEmpty()) {
            Text(actionMessage, color = MaterialTheme.colorScheme.error)
        }
        Text(
            text = "Pending Claims",
            style = MaterialTheme.typography.titleLarge
        )

        val pendingClaims = claims.filter { it.status == ClaimStatus.PENDING }
        if (pendingClaims.isEmpty()) {
            Text("There are no pending claims.")
        } else {
            pendingClaims.forEach { claim ->

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
                                    onClick = { reviewClaim(claim, approve = true) },
                                    enabled = actionInProgress == null
                                ) {
                                    Text(if (actionInProgress == claim.id) "Saving..." else "Approve")
                                }

                                OutlinedButton(
                                    onClick = { reviewClaim(claim, approve = false) },
                                    enabled = actionInProgress == null
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

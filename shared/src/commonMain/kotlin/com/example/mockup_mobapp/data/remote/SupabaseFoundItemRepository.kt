
package com.example.mockup_mobapp.data.remote

import com.example.mockup_mobapp.data.model.FoundItem
import com.example.mockup_mobapp.data.model.ItemStatus
import com.example.mockup_mobapp.data.model.ReportType
import com.example.mockup_mobapp.data.model.Claim
import com.example.mockup_mobapp.data.model.ClaimStatus
import com.example.mockup_mobapp.domain.repository.FoundItemRepository

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

private const val IDENTIFYING_DETAILS_SEPARATOR = "\n\nIdentifying details:\n"
private const val REASON_PREFIX = "Reason:\n"

class SupabaseFoundItemRepository : FoundItemRepository {

    override suspend fun getFoundItems(): List<FoundItem> {
        val remoteItems = supabase
            .from("items")
            .select()
            .decodeList<SupabaseItem>()

        return remoteItems.map { item ->
            FoundItem(
                id = item.id,
                itemName = item.itemName,
                category = item.category,
                description = item.description,
                location = item.location,
                dateFound = item.dateFound.orEmpty(),
                reportType = ReportType.valueOf(
                    item.reportType.trim().uppercase()
                ),
                status = ItemStatus.valueOf(
                    item.status.trim().uppercase()
                )
            )
        }
    }

    override suspend fun addFoundItem(item: FoundItem) {
        val currentUser = supabase.auth.currentUserOrNull()
            ?: throw IllegalStateException(
                "You must be logged in to submit a report."
            )

        val newItem = SupabaseItemInsert(
            itemName = item.itemName,
            category = item.category,
            description = item.description,
            location = item.location,
            dateFound = item.dateFound,
            reportType = item.reportType.name,
            status = item.status.name,
            userId = currentUser.id
        )

        supabase
            .from("items")
            .insert(newItem)
    }

    override suspend fun getClaims(): List<Claim> =
        supabase
            .from("claims")
            .select()
            .decodeList<SupabaseClaim>()
            .map { claim ->
                val (reason, identifyingDetails) = claim.claimDescription.toClaimTextParts()
                Claim(
                    id = claim.id,
                    itemId = claim.itemId,
                    claimantId = claim.claimantId,
                    claimReason = reason,
                    identifyingDetails = identifyingDetails,
                    status = claim.status.toClaimStatus(),
                    createdAt = claim.createdAt
                )
            }

    override suspend fun addClaim(claim: Claim) {
        supabase.auth.currentUserOrNull()
            ?: throw IllegalStateException("You must be logged in to submit a claim.")

        supabase.postgrest.rpc(
            "submit_claim",
            SubmitClaimParams(
                itemId = claim.itemId,
                claimDescription = claim.toSupabaseDescription()
            )
        )
    }

    override suspend fun updateClaimStatus(
        claimId: String,
        status: ClaimStatus
    ) {
        supabase
            .from("claims")
            .update({ set("status", status.toSupabaseValue()) }) {
                filter { eq("id", claimId) }
            }
    }

    override suspend fun rejectOtherPendingClaims(
        itemId: String,
        approvedClaimId: String
    ) {
        supabase
            .from("claims")
            .update({ set("status", ClaimStatus.REJECTED.name) }) {
                filter {
                    eq("item_id", itemId)
                    eq("status", ClaimStatus.PENDING.toSupabaseValue())
                    neq("id", approvedClaimId)
                }
            }
    }

    override suspend fun updateItemStatus(
        itemId: String,
        status: ItemStatus
    ) {
        val updatedItems = supabase
            .from("items")
            .update({ set("status", status.name) }) {
                select()
                filter { eq("id", itemId) }
            }
            .decodeList<SupabaseItem>()

        if (updatedItems.isEmpty()) {
            throw IllegalStateException(
                "The item status was not updated. Check the item and your Supabase permissions."
            )
        }
    }
}

private fun Claim.toSupabaseDescription(): String =
    "$REASON_PREFIX$claimReason$IDENTIFYING_DETAILS_SEPARATOR$identifyingDetails"

private fun String.toClaimTextParts(): Pair<String, String> {
    val separatorIndex = indexOf(IDENTIFYING_DETAILS_SEPARATOR)
    if (separatorIndex < 0) return removePrefix(REASON_PREFIX) to ""

    val reason = substring(0, separatorIndex).removePrefix(REASON_PREFIX)
    val identifyingDetails = substring(separatorIndex + IDENTIFYING_DETAILS_SEPARATOR.length)
    return reason to identifyingDetails
}

private fun String.toClaimStatus(): ClaimStatus = when (trim().uppercase()) {
    "PENDING", "PENDING_REVIEW" -> ClaimStatus.PENDING
    "APPROVED" -> ClaimStatus.APPROVED
    "REJECTED" -> ClaimStatus.REJECTED
    else -> throw IllegalArgumentException("Unknown claim status: $this")
}

private fun ClaimStatus.toSupabaseValue(): String = when (this) {
    ClaimStatus.PENDING -> "PENDING_REVIEW"
    ClaimStatus.APPROVED -> "APPROVED"
    ClaimStatus.REJECTED -> "REJECTED"
}

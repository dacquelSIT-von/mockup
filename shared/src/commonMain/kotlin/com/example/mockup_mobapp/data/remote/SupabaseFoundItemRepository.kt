
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

class SupabaseFoundItemRepository(
    private val localRepository: FoundItemRepository
) : FoundItemRepository {

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
                dateFound = item.dateFound,
                reportType = ReportType.valueOf(
                    item.reportType.trim().uppercase()
                ),
                status = ItemStatus.valueOf(
                    item.status.trim().uppercase()
                ),
                userId = item.userId
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

    override suspend fun getClaims(): List<Claim> {
        val remoteClaims = supabase
            .from("claims")
            .select()
            .decodeList<SupabaseClaim>()

        return remoteClaims.map { claim ->
            val status = when (claim.status.trim().uppercase()) {
                "PENDING_REVIEW" -> ClaimStatus.PENDING
                "APPROVED" -> ClaimStatus.APPROVED
                "REJECTED" -> ClaimStatus.REJECTED
                else -> throw IllegalStateException(
                    "Unsupported claim status '${claim.status}' for claim ${claim.id}."
                )
            }

            Claim(
                id = claim.id,
                itemId = claim.itemId,
                claimantId = claim.claimantId,
                claimReason = claim.claimReason.orEmpty(),
                identifyingDetails = claim.identifyingDetails.orEmpty(),
                status = status,
                createdAt = claim.createdAt
            )
        }
    }

    override suspend fun addClaim(claim: Claim) {
        supabase.postgrest.rpc(
            function = "submit_claim",
            parameters = SupabaseSubmitClaimParams(
                itemId = claim.itemId,
                claimReason = claim.claimReason,
                identifyingDetails = claim.identifyingDetails
            )
        )
    }

    override suspend fun reviewClaim(claimId: String, decision: String) {
        supabase.postgrest.rpc(
            function = "review_claim",
            parameters = SupabaseReviewClaimParams(
                claimId = claimId,
                decision = decision
            )
        )
    }

    override suspend fun updateClaimStatus(
        claimId: String,
        status: ClaimStatus
    ) = localRepository.updateClaimStatus(claimId, status)

    override suspend fun rejectOtherPendingClaims(
        itemId: String,
        approvedClaimId: String
    ) = localRepository.rejectOtherPendingClaims(itemId, approvedClaimId)

    override suspend fun updateItemStatus(
        itemId: String,
        status: ItemStatus
    ) = localRepository.updateItemStatus(itemId, status)
}

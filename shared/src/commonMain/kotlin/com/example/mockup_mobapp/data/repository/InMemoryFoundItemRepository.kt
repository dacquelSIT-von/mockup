
package com.example.mockup_mobapp.data.repository

import com.example.mockup_mobapp.data.model.FoundItem
import com.example.mockup_mobapp.domain.repository.FoundItemRepository

import com.example.mockup_mobapp.data.model.Claim
import com.example.mockup_mobapp.data.model.ClaimStatus
import com.example.mockup_mobapp.data.model.ItemStatus

class InMemoryFoundItemRepository : FoundItemRepository {

    private val foundItems = mutableListOf<FoundItem>()

    private val claims = mutableListOf<Claim>()

    override suspend fun getFoundItems(): List<FoundItem> {
        return foundItems.toList()
    }

    override suspend fun addFoundItem(item: FoundItem) {
        foundItems.add(item)
    }
    override suspend fun getClaims(): List<Claim> {
        return claims.toList()
    }

    override suspend fun addClaim(claim: Claim) {
        claims.add(claim)
    }

    override suspend fun reviewClaim(claimId: String, decision: String) {
        throw UnsupportedOperationException(
            "Staff claim review is only supported by the Supabase repository."
        )
    }

    override suspend fun updateClaimStatus(
        claimId: String,
        status: ClaimStatus
    ) {
        val index = claims.indexOfFirst { it.id == claimId }

        if (index != -1) {
            claims[index] = claims[index].copy(status = status)
        }
    }

    override suspend fun updateItemStatus(
        itemId: String,
        status: ItemStatus
    ) {
        val index = foundItems.indexOfFirst { it.id == itemId }

        if (index != -1) {
            foundItems[index] = foundItems[index].copy(status = status)
        }
    }
    override suspend fun rejectOtherPendingClaims(
        itemId: String,
        approvedClaimId: String
    ) {
        claims.indices.forEach { index ->
            val claim = claims[index]

            if (
                claim.itemId == itemId &&
                claim.id != approvedClaimId &&
                claim.status == ClaimStatus.PENDING
            ) {
                claims[index] = claim.copy(
                    status = ClaimStatus.REJECTED
                )
            }
        }
    }
}

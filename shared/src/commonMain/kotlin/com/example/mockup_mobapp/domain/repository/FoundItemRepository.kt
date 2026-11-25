
package com.example.mockup_mobapp.domain.repository
import com.example.mockup_mobapp.data.model.FoundItem
import com.example.mockup_mobapp.data.model.Claim
import com.example.mockup_mobapp.data.model.ClaimStatus
import com.example.mockup_mobapp.data.model.ItemStatus

interface FoundItemRepository {

    suspend fun getFoundItems(): List<FoundItem>

    suspend fun addFoundItem(item: FoundItem)

    suspend fun getClaims(): List<Claim>

    suspend fun addClaim(claim: Claim)

    suspend fun reviewClaim(
        claimId: String,
        decision: String
    )

    suspend fun updateClaimStatus(
        claimId: String,
        status: ClaimStatus
    )

    suspend fun rejectOtherPendingClaims(
        itemId: String,
        approvedClaimId: String
    )

    suspend fun updateItemStatus(
        itemId: String,
        status: ItemStatus
    )
}

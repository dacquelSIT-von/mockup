
package com.example.mockup_mobapp.data.remote

import com.example.mockup_mobapp.data.model.FoundItem
import com.example.mockup_mobapp.data.model.ItemStatus
import com.example.mockup_mobapp.data.model.ReportType
import com.example.mockup_mobapp.data.model.Claim
import com.example.mockup_mobapp.data.model.ClaimStatus
import com.example.mockup_mobapp.data.repository.SqlDelightFoundItemRepository
import com.example.mockup_mobapp.domain.repository.FoundItemRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

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
                reportType = ReportType.valueOf(item.reportType.trim().uppercase()),
                status = ItemStatus.valueOf(item.status.trim().uppercase())
            )
        }
    }

    override suspend fun addFoundItem(item: FoundItem) {
        localRepository.addFoundItem(item)
    }

    override suspend fun getClaims(): List<Claim> =
        localRepository.getClaims()

    override suspend fun addClaim(claim: Claim) =
        localRepository.addClaim(claim)

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

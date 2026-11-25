package com.example.mockup_mobapp.data.repository

import com.example.mockup_mobapp.data.local.AppDatabase
import com.example.mockup_mobapp.data.model.FoundItem
import com.example.mockup_mobapp.data.model.ItemStatus
import com.example.mockup_mobapp.data.model.ReportType
import com.example.mockup_mobapp.domain.repository.FoundItemRepository

import com.example.mockup_mobapp.data.model.Claim
import com.example.mockup_mobapp.data.model.ClaimStatus



class SqlDelightFoundItemRepository(
    private val database: AppDatabase
) : FoundItemRepository {
    override suspend fun getFoundItems(): List<FoundItem> {
        return database.itemQueries.selectAllItems()
            .executeAsList()
            .map { item ->
                FoundItem(
                    id = item.id,
                    itemName = item.item_name,
                    category = item.category,
                    description = item.description,
                    location = item.location,
                    dateFound = item.date_found,
                    reportType = ReportType.valueOf(item.report_type),
                    status = ItemStatus.valueOf(item.status)
                )
            }
    }
    override suspend fun addFoundItem(item: FoundItem) {
        database.itemQueries.insertItem(
            id = item.id,
            item_name = item.itemName,
            report_type = item.reportType.name,
            category = item.category,
            description = item.description,
            location = item.location,
            date_found = item.dateFound,
            created_at = kotlin.time.Clock.System.now().toString(),
            status = item.status.name,
            user_id = null,
            sync_status = "PENDING"
        )
    }
    override suspend fun getClaims(): List<Claim> {
        return database.itemQueries.selectAllClaims()
            .executeAsList()
            .map { claim ->
                Claim(
                    id = claim.id,
                    itemId = claim.item_id,
                    claimantId = claim.claimant_id,
                    claimReason = claim.claim_reason,
                    identifyingDetails = claim.identifying_details,
                    status = ClaimStatus.valueOf(claim.status),
                    createdAt = claim.created_at
                )
            }
    }

    override suspend fun addClaim(claim: Claim) {
        database.itemQueries.insertClaim(
            id = claim.id,
            item_id = claim.itemId,
            claimant_id = claim.claimantId,
            claim_reason = claim.claimReason,
            identifying_details = claim.identifyingDetails,
            status = claim.status.name,
            created_at = claim.createdAt
        )
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
        database.itemQueries.updateClaimStatus(
            status = status.name,
            id = claimId
        )
    }

    override suspend fun updateItemStatus(
        itemId: String,
        status: ItemStatus
    ) {
        database.itemQueries.updateItemStatus(
            status = status.name,
            id = itemId
        )
    }

    override suspend fun rejectOtherPendingClaims(
        itemId: String,
        approvedClaimId: String
    ) {
        database.itemQueries.rejectOtherPendingClaims(
            itemId,
            approvedClaimId
        )
    }
    
    
}

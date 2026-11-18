
package com.example.mockup_mobapp.data.repository

import com.example.mockup_mobapp.data.model.FoundItem
import com.example.mockup_mobapp.domain.repository.FoundItemRepository

import com.example.mockup_mobapp.data.model.Claim

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
}

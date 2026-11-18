
package com.example.mockup_mobapp.domain.repository

import com.example.mockup_mobapp.data.model.FoundItem

import com.example.mockup_mobapp.data.model.Claim

interface FoundItemRepository {

    suspend fun getFoundItems(): List<FoundItem>

    suspend fun addFoundItem(item: FoundItem)

    suspend fun getClaims(): List<Claim>

    suspend fun addClaim(claim: Claim)
}

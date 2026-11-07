
package com.example.mockup_mobapp.domain.repository

import com.example.mockup_mobapp.data.model.FoundItem

interface FoundItemRepository {

    suspend fun getFoundItems(): List<FoundItem>

    suspend fun addFoundItem(item: FoundItem)
}


package com.example.mockup_mobapp.data.repository

import com.example.mockup_mobapp.data.model.FoundItem
import com.example.mockup_mobapp.domain.repository.FoundItemRepository

class InMemoryFoundItemRepository : FoundItemRepository {

    private val foundItems = mutableListOf<FoundItem>()

    override suspend fun getFoundItems(): List<FoundItem> {
        return foundItems.toList()
    }

    override suspend fun addFoundItem(item: FoundItem) {
        foundItems.add(item)
    }
}

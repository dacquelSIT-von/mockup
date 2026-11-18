package com.example.mockup_mobapp.data.repository

import com.example.mockup_mobapp.data.local.AppDatabase
import com.example.mockup_mobapp.data.model.FoundItem
import com.example.mockup_mobapp.data.model.ItemStatus
import com.example.mockup_mobapp.data.model.ReportType
import com.example.mockup_mobapp.domain.repository.FoundItemRepository

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
}
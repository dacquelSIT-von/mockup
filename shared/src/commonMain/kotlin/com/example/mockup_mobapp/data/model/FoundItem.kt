
package com.example.mockup_mobapp.data.model

enum class ItemStatus {
    AVAILABLE,
    CLAIM_PENDING,
    RETURNED
}

enum class ReportType {
    LOST,
    FOUND
}

data class FoundItem(
    val id: String,
    val itemName: String,
    val category: String,
    val description: String,
    val location: String,
    val dateFound: String,
    val reportType: ReportType = ReportType.FOUND,
    val status: ItemStatus = ItemStatus.AVAILABLE
)

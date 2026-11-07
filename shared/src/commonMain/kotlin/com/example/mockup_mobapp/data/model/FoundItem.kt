
package com.example.mockup_mobapp.data.model

enum class ItemStatus {
    AVAILABLE,
    CLAIM_PENDING,
    RETURNED
}

data class FoundItem(
    val id: String,
    val itemName: String,
    val category: String,
    val description: String,
    val location: String,
    val dateFound: String,
    val status: ItemStatus = ItemStatus.AVAILABLE
)

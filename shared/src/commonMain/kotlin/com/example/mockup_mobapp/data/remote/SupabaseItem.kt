package com.example.mockup_mobapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseItem(
    val id: String,

    @SerialName("item_name")
    val itemName: String,

    val category: String,
    val description: String,
    val location: String,

    @SerialName("date_found")
    val dateFound: String,

    @SerialName("report_type")
    val reportType: String,

    val status: String
)
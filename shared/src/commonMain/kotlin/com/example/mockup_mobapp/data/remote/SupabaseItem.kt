
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
    val dateFound: String?,

    @SerialName("report_type")
    val reportType: String,

    val status: String
)

@Serializable
data class SupabaseItemInsert(
    @SerialName("item_name")
    val itemName: String,

    val category: String,
    val description: String,
    val location: String,

    @SerialName("date_found")
    val dateFound: String,

    @SerialName("report_type")
    val reportType: String,

    val status: String,

    @SerialName("user_id")
    val userId: String
)

@Serializable
data class SupabaseClaim(
    val id: String,

    @SerialName("item_id")
    val itemId: String,

    @SerialName("claimant_id")
    val claimantId: String? = null,

    @SerialName("claim_description")
    val claimDescription: String,

    val status: String,

    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class SubmitClaimParams(
    @SerialName("p_item_id")
    val itemId: String,

    @SerialName("p_claim_description")
    val claimDescription: String
)

package com.example.mockup_mobapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseClaim(
    val id: String,

    @SerialName("item_id")
    val itemId: String,

    @SerialName("claimant_id")
    val claimantId: String,

    @SerialName("claim_description")
    val claimDescription: String,

    @SerialName("claim_reason")
    val claimReason: String?,

    @SerialName("identifying_details")
    val identifyingDetails: String?,

    val status: String,

    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class SupabaseClaimInsert(
    @SerialName("item_id")
    val itemId: String,

    @SerialName("claim_description")
    val claimDescription: String,

    @SerialName("claim_reason")
    val claimReason: String,

    @SerialName("identifying_details")
    val identifyingDetails: String
)

@Serializable
data class SupabaseSubmitClaimParams(
    @SerialName("p_item_id")
    val itemId: String,

    @SerialName("p_claim_reason")
    val claimReason: String,

    @SerialName("p_identifying_details")
    val identifyingDetails: String
)

@Serializable
data class SupabaseReviewClaimParams(
    @SerialName("p_claim_id")
    val claimId: String,

    @SerialName("p_decision")
    val decision: String
)

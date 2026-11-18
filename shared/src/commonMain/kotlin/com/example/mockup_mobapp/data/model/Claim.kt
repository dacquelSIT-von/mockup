package com.example.mockup_mobapp.data.model

enum class ClaimStatus {
    PENDING,
    APPROVED,
    REJECTED
}

data class Claim(
    val id: String,
    val itemId: String,
    val claimantId: String? = null,
    val claimReason: String,
    val identifyingDetails: String,
    val status: ClaimStatus = ClaimStatus.PENDING,
    val createdAt: String
)
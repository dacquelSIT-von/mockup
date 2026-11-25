package com.example.mockup_mobapp.data.model

enum class UserRole {
    STUDENT,
    STAFF,
    UNKNOWN
}

data class UserProfile(
    val id: String,
    val fullName: String,
    val role: UserRole
)

fun normalizeUserRole(value: String?): UserRole =
    when (value?.trim()?.uppercase()) {
        "STUDENT" -> UserRole.STUDENT
        "STAFF" -> UserRole.STAFF
        else -> UserRole.UNKNOWN
    }

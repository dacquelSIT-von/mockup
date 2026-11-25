package com.example.mockup_mobapp.data.remote

import com.example.mockup_mobapp.data.model.UserProfile
import com.example.mockup_mobapp.data.model.normalizeUserRole
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SupabaseProfileRepository {

    suspend fun getCurrentProfile(): UserProfile? {
        val user = supabase.auth.currentUserOrNull()
            ?: throw IllegalStateException("You are not authenticated.")

        val profile = supabase
            .from("profiles")
            .select {
                filter {
                    eq("id", user.id)
                }
            }
            .decodeSingleOrNull<SupabaseProfile>()
            ?: return null

        return UserProfile(
            id = profile.id,
            fullName = profile.fullName?.takeIf { it.isNotBlank() } ?: "Name unavailable",
            role = normalizeUserRole(profile.role)
        )
    }
}

@Serializable
private data class SupabaseProfile(
    val id: String,
    @SerialName("full_name") val fullName: String?,
    val role: String?
)

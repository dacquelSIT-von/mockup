
package com.example.mockup_mobapp.data.remote

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthService {

    suspend fun register(
        fullName: String,
        email: String,
        password: String
    ) {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password

            data = buildJsonObject {
                put("full_name", fullName)
            }
        }
    }
    
    suspend fun login(
        email: String,
        password: String
    ) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }
    
    suspend fun logout() {
        supabase.auth.signOut()
    }

}

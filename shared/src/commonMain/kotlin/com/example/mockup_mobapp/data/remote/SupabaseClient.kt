
package com.example.mockup_mobapp.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin


val supabase = createSupabaseClient(
    supabaseUrl = SupabaseConfig.URL,
    supabaseKey = SupabaseConfig.PUBLISHABLE_KEY
) {
    install(ComposeAuth) {
        googleNativeLogin(serverClientId = "110235859845-tgp3frji15nnumnl56c60426dc9uv1j0.apps.googleusercontent.com")
    }
    install(Auth)
    install(Postgrest)
}

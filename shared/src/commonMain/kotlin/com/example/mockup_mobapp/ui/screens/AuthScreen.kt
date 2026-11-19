
package com.example.mockup_mobapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mockup_mobapp.data.remote.AuthService
import kotlinx.coroutines.launch
import com.example.mockup_mobapp.data.remote.supabase
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth

@Composable
fun AuthScreen() {

    val authService = remember { AuthService() }
    val coroutineScope = rememberCoroutineScope()
    
    val googleAuthState = supabase.composeAuth.rememberSignInWithGoogle()

    var isRegistering by remember { mutableStateOf(false) }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var message by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = if (isRegistering) "Create Account" else "Welcome Back",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Lost & Found SIT Dept.",
            style = MaterialTheme.typography.titleMedium
        )

        if (isRegistering) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                message = ""

                if (
                    email.isBlank() ||
                    password.isBlank() ||
                    (isRegistering && fullName.isBlank())
                ) {
                    message = "Please complete all required fields."
                    return@Button
                }

                coroutineScope.launch {
                    isSubmitting = true

                    try {
                        if (isRegistering) {
                            authService.register(
                                fullName = fullName.trim(),
                                email = email.trim(),
                                password = password
                            )

                            message = "Registration successful! Please check your email to confirm your account."
                        } else {
                            authService.login(
                                email = email.trim(),
                                password = password
                            )

                            message = "Login successful!"
                        }
                    } catch (e: Exception) {
                        message = buildString {
                            appendLine("Authentication failed.")
                            appendLine("Error: ${e::class.simpleName}")
                            appendLine("Details: ${e.message ?: e.toString()}")
                        }
                        e.printStackTrace()
                    } finally {
                        isSubmitting = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting
        ) {
            Text(
                if (isSubmitting) "Please wait..."
                else if (isRegistering) "Register"
                else "Login"
            )
        }
        
        OutlinedButton(
            onClick = {
                googleAuthState.startFlow()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting
        ) {
            Text("Continue with Google")
        }

        TextButton(
            onClick = {
                isRegistering = !isRegistering
                message = ""
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting
        ) {
            Text(
                if (isRegistering)
                    "Already have an account? Login"
                else
                    "Don't have an account? Register"
            )
        }

        if (message.isNotBlank()) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

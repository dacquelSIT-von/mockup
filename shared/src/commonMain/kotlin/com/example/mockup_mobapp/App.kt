
package com.example.mockup_mobapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment

import com.example.mockup_mobapp.ui.screens.FindItemScreen
import com.example.mockup_mobapp.ui.screens.HomeScreen
import com.example.mockup_mobapp.ui.screens.ReportItemScreen
import com.example.mockup_mobapp.ui.screens.ItemDetailsScreen
import com.example.mockup_mobapp.data.model.FoundItem
import com.example.mockup_mobapp.ui.screens.MyAccountScreen
import com.example.mockup_mobapp.ui.screens.MyActivityScreen
import com.example.mockup_mobapp.ui.screens.ReportProblemScreen
import com.example.mockup_mobapp.data.local.AppDatabase
import com.example.mockup_mobapp.domain.repository.FoundItemRepository
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.collect
import io.github.jan.supabase.auth.auth
import com.example.mockup_mobapp.ui.screens.AuthScreen
import androidx.compose.material3.CircularProgressIndicator
import com.example.mockup_mobapp.data.remote.AuthService
import com.example.mockup_mobapp.data.remote.SupabaseFoundItemRepository

//staff
import com.example.mockup_mobapp.ui.screens.StaffClaimScreen

//supabase
import com.example.mockup_mobapp.data.remote.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

import kotlinx.coroutines.withTimeout

@Composable
fun App(database: AppDatabase) {
        var isAuthenticated by remember {
        mutableStateOf(false)
    }

    var isCheckingAuth by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        supabase.auth.sessionStatus.collect { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    isAuthenticated = true
                    isCheckingAuth = false
                }

                is SessionStatus.NotAuthenticated -> {
                    isAuthenticated = false
                    isCheckingAuth = false
                }

                is SessionStatus.Initializing -> {
                    isCheckingAuth = true
                }

                is SessionStatus.RefreshFailure -> {
                    isAuthenticated = false
                    isCheckingAuth = false
                }
            }
        }
    }
    var supabaseStatus by remember {
        mutableStateOf("Checking Supabase connection...")
    }

    LaunchedEffect(Unit) {
        try {
            withTimeout(15_000) {
                supabase.from("items").select()
            }

            supabaseStatus = "Supabase connection successful!"
        } catch (e: Exception) {
            supabaseStatus = buildString {
                appendLine("Connection failed")
                appendLine("Type: ${e::class.simpleName}")
                appendLine("Message: ${e.message}")
                appendLine("Cause: ${e.cause?.message}")
            }

            e.printStackTrace()
        }
    }

    val itemRepository = remember(database) {
        SupabaseFoundItemRepository()
    }

    var currentScreen by remember {
        mutableStateOf(Screen.HOME)
    }

    var selectedItem by remember {
        mutableStateOf<FoundItem?>(null)
    }

    MaterialTheme {
    when {
        isCheckingAuth -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        !isAuthenticated -> {
            AuthScreen()
        }

        else -> {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
            val isDesktop = maxWidth >= 600.dp

            if (isDesktop) {
                Row(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(
                        currentScreen = currentScreen,
                        onNavigate = { currentScreen = it },
                        isDesktop = true
                    )

                    ScreenContent(
                        currentScreen = currentScreen,
                        onNavigate = { currentScreen = it },
                        modifier = Modifier.weight(1f),
                        selectedItem = selectedItem,
                        onItemSelected = { item ->
                            selectedItem = item
                        },
                        itemRepository = itemRepository
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    ScreenContent(
                        currentScreen = currentScreen,
                        onNavigate = { currentScreen = it },
                        modifier = Modifier.weight(1f),
                        selectedItem = selectedItem,
                        onItemSelected = { item ->
                            selectedItem = item
                        },
                        itemRepository = itemRepository
                    )

                    AppNavigation(
                        currentScreen = currentScreen,
                        onNavigate = { currentScreen = it },
                        isDesktop = false
                    )
                }
            }
                        Text(
                text = supabaseStatus,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(8.dp)
            )
            } // BoxWithConstraints
        } // else
    } // when
} // MaterialTheme
} // App

@Composable
fun ScreenContent(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier,
    selectedItem: FoundItem?,
    onItemSelected: (FoundItem) -> Unit,
    itemRepository: FoundItemRepository,
) {
    val coroutineScope = rememberCoroutineScope()
    
    Box(modifier = modifier.fillMaxSize()) {
        when (currentScreen) {
            Screen.HOME -> HomeScreen(
                onFindItem = {
                    onNavigate(Screen.FIND_ITEM)
                },
                onReportItem = {
                    onNavigate(Screen.REPORT_ITEM)
                },
                repository = itemRepository
            )

            Screen.FIND_ITEM -> FindItemScreen(
                repository = itemRepository,
                onBack = { onNavigate(Screen.HOME) },
                onItemSelected = { item ->
                    onItemSelected(item)
                    onNavigate(Screen.ITEM_DETAILS)
                }
            )

            Screen.REPORT_ITEM ->
                ReportItemScreen(
                    repository = itemRepository,
                    onGoHome = {
                        onNavigate(Screen.HOME)
                    }
                )

            Screen.ITEM_DETAILS -> {
                selectedItem?.let { item ->
                    ItemDetailsScreen(
                        item = item,
                        repository = itemRepository,
                        onReportProblem = {
                            onNavigate(Screen.REPORT_PROBLEM)
                        }
                    )
                }
            }

            Screen.REPORT_PROBLEM -> {
                selectedItem?.let { item ->
                    ReportProblemScreen(
                        item = item,
                        onBack = {
                            onNavigate(Screen.ITEM_DETAILS)
                        }
                    )
                }
            }

            Screen.MY_ACTIVITY ->
                MyActivityScreen(
                    repository = itemRepository
                )

           Screen.MY_ACCOUNT ->
            MyAccountScreen(
                onLogout = {
                    coroutineScope.launch {
                        AuthService().logout()
                    }
                }
            )

            Screen.STAFF_CLAIMS ->
                StaffClaimScreen(
                    repository = itemRepository
                )
            
                
        }
    }
}

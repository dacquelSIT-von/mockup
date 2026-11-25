
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
import com.example.mockup_mobapp.data.repository.SqlDelightFoundItemRepository
import com.example.mockup_mobapp.domain.repository.FoundItemRepository
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.collectLatest
import io.github.jan.supabase.auth.auth
import com.example.mockup_mobapp.ui.screens.AuthScreen
import androidx.compose.material3.CircularProgressIndicator
import com.example.mockup_mobapp.data.remote.AuthService
import com.example.mockup_mobapp.data.remote.SupabaseFoundItemRepository
import com.example.mockup_mobapp.data.remote.SupabaseProfileRepository
import com.example.mockup_mobapp.data.model.UserProfile

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

    var profile by remember { mutableStateOf<UserProfile?>(null) }
    var profileState by remember { mutableStateOf(ProfileLoadState.Idle) }
    var profileRetryKey by remember { mutableStateOf(0) }
    val profileRepository = remember { SupabaseProfileRepository() }
    val appScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        supabase.auth.sessionStatus.collectLatest { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    isAuthenticated = true
                    isCheckingAuth = false
                    profile = null
                    profileState = ProfileLoadState.Loading
                }

                is SessionStatus.NotAuthenticated -> {
                    isAuthenticated = false
                    isCheckingAuth = false
                    profile = null
                    profileState = ProfileLoadState.Idle
                }

                is SessionStatus.Initializing -> {
                    isCheckingAuth = true
                }

                is SessionStatus.RefreshFailure -> {
                    isAuthenticated = false
                    isCheckingAuth = false
                    profile = null
                    profileState = ProfileLoadState.Idle
                }
            }
        }
    }

    LaunchedEffect(isAuthenticated, profileRetryKey, profileRepository) {
        if (isAuthenticated) {
            profileState = ProfileLoadState.Loading
            try {
                profile = profileRepository.getCurrentProfile()
                profileState = if (profile == null) {
                    ProfileLoadState.Missing
                } else {
                    ProfileLoadState.Loaded
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (_: Exception) {
                profile = null
                profileState = ProfileLoadState.Error
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
        val localRepository = SqlDelightFoundItemRepository(database)

        SupabaseFoundItemRepository(localRepository)
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

        profileState != ProfileLoadState.Loaded || profile == null -> {
            ProfileStatusScreen(
                state = profileState,
                onRetry = { profileRetryKey++ },
                onLogout = {
                    appScope.launch {
                        try {
                            AuthService().logout()
                        } catch (_: Exception) {
                            profileState = ProfileLoadState.Error
                        }
                    }
                }
            )
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
                        profile = requireNotNull(profile),
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
                        profile = requireNotNull(profile),
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
    profile: UserProfile,
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
                profile = profile,
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

private enum class ProfileLoadState {
    Idle,
    Loading,
    Loaded,
    Missing,
    Error
}

@Composable
private fun ProfileStatusScreen(
    state: ProfileLoadState,
    onRetry: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (state) {
            ProfileLoadState.Idle, ProfileLoadState.Loading -> {
                CircularProgressIndicator()
                Text("Loading your profile…")
            }
            ProfileLoadState.Missing -> {
                Text("Your account profile was not found.")
                Text("Please contact support to restore your profile.")
                androidx.compose.material3.Button(onClick = onLogout) {
                    Text("Log out")
                }
            }
            ProfileLoadState.Error -> {
                Text("Unable to load your profile. Check your connection and try again.")
                androidx.compose.material3.Button(onClick = onRetry) {
                    Text("Retry")
                }
                androidx.compose.material3.TextButton(onClick = onLogout) {
                    Text("Log out")
                }
            }
            ProfileLoadState.Loaded -> Unit
        }
    }
}

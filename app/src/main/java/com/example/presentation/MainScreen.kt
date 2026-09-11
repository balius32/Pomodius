package com.example.presentation

import android.Manifest
import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.AppContainer
import com.example.presentation.components.NavDestination
import com.example.presentation.components.NeoBottomNavBar
import com.example.presentation.focus.FocusEvent
import com.example.presentation.focus.FocusScreen
import com.example.presentation.focus.FocusViewModel
import com.example.presentation.insights.InsightsScreen
import com.example.presentation.insights.InsightsViewModel
import com.example.presentation.settings.SettingsScreen
import com.example.presentation.settings.SettingsViewModel
import com.example.presentation.standby.StandbyScreen
import com.example.presentation.tasks.TasksScreen
import com.example.presentation.tasks.TasksViewModel
import com.example.ui.theme.BoneCanvas

@Composable
fun MainScreen(
    container: AppContainer,
    modifier: Modifier = Modifier
) {
    val factory = remember(container) { AppViewModelFactory(container) }

    val focusViewModel: FocusViewModel = viewModel(factory = factory)
    val tasksViewModel: TasksViewModel = viewModel(factory = factory)
    val insightsViewModel: InsightsViewModel = viewModel(factory = factory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)

    val focusState by focusViewModel.uiState.collectAsStateWithLifecycle()
    val tasksState by tasksViewModel.uiState.collectAsStateWithLifecycle()
    val insightsState by insightsViewModel.uiState.collectAsStateWithLifecycle()
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    var currentDestination by rememberSaveable { mutableStateOf(NavDestination.FOCUS) }
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val shouldShowStandby = focusState.isStandbyOpen || (isLandscape && settingsState.preferences.standbyOnLandscape)

    // Back Press Management
    // 1. Standby mode open -> Close Standby mode
    BackHandler(enabled = shouldShowStandby) {
        focusViewModel.onEvent(FocusEvent.CloseStandby)
    }

    // 2. Add/Edit Task dialog open on Tasks screen -> Close dialog
    BackHandler(enabled = !shouldShowStandby && currentDestination == NavDestination.TASKS && tasksState.isAddDialogOpen) {
        tasksViewModel.onEvent(com.example.presentation.tasks.TasksEvent.CloseDialog)
    }

    // 3. Celebration dialog open on Focus screen -> Dismiss celebration
    BackHandler(enabled = !shouldShowStandby && currentDestination == NavDestination.FOCUS && focusState.showCelebrationDialog) {
        focusViewModel.onEvent(FocusEvent.DismissCelebration)
    }

    // 4. On secondary tab (Tasks, Insights, Settings) -> Navigate back to Focus screen
    BackHandler(enabled = !shouldShowStandby && currentDestination != NavDestination.FOCUS && !tasksState.isAddDialogOpen) {
        currentDestination = NavDestination.FOCUS
    }

    // 5. On Focus screen -> Exit/close the application
    BackHandler(enabled = !shouldShowStandby && currentDestination == NavDestination.FOCUS && !focusState.showCelebrationDialog) {
        (context as? Activity)?.finish()
    }

    // Request POST_NOTIFICATIONS permission on Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { _ -> }
        LaunchedEffect(Unit) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (shouldShowStandby) {
        StandbyScreen(
            state = focusState,
            onEvent = focusViewModel::onEvent,
            onDismiss = { focusViewModel.onEvent(FocusEvent.CloseStandby) }
        )
    } else {
        Scaffold(
            bottomBar = {
                NeoBottomNavBar(
                    currentDestination = currentDestination,
                    onNavigate = { destination ->
                        currentDestination = destination
                    }
                )
            },
            modifier = modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BoneCanvas)
                    .padding(paddingValues)
            ) {
                Crossfade(targetState = currentDestination, label = "ScreenTransition") { destination ->
                    when (destination) {
                        NavDestination.FOCUS -> FocusScreen(
                            state = focusState,
                            onEvent = focusViewModel::onEvent,
                            onNavigateToTasks = { currentDestination = NavDestination.TASKS },
                            onOpenStandby = { focusViewModel.onEvent(FocusEvent.OpenStandby) }
                        )
                        NavDestination.TASKS -> TasksScreen(
                            state = tasksState,
                            onEvent = tasksViewModel::onEvent,
                            onOpenStandby = { focusViewModel.onEvent(FocusEvent.OpenStandby) }
                        )
                        NavDestination.INSIGHTS -> InsightsScreen(
                            state = insightsState,
                            onExportTelemetry = insightsViewModel::exportTelemetry,
                            onOpenStandby = { focusViewModel.onEvent(FocusEvent.OpenStandby) }
                        )
                        NavDestination.SETTINGS -> SettingsScreen(
                            state = settingsState,
                            onEvent = settingsViewModel::onEvent,
                            onOpenStandby = { focusViewModel.onEvent(FocusEvent.OpenStandby) }
                        )
                    }
                }
            }
        }
    }
}

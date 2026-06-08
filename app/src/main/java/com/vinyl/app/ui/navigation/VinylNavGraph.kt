package com.vinyl.app.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vinyl.app.ui.idle.IdleScreen
import com.vinyl.app.ui.nowplaying.NowPlayingScreen
import com.vinyl.app.ui.nowplaying.NowPlayingUiState
import com.vinyl.app.ui.onboarding.PermissionScreen
import com.vinyl.app.ui.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Permission : Screen("permission")
    object Idle : Screen("idle")
    object NowPlaying : Screen("nowPlaying")
    object Settings : Screen("settings")
}

@Composable
fun VinylNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Idle.route
) {
    val activity = LocalContext.current as ComponentActivity
    val viewModel = hiltViewModel<com.vinyl.app.ui.nowplaying.NowPlayingViewModel>(activity)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        when (uiState) {
            is NowPlayingUiState.Playing -> {
                if (navController.currentDestination?.route != Screen.NowPlaying.route) {
                    navController.navigate(Screen.NowPlaying.route) {
                        popUpTo(Screen.Idle.route) { inclusive = true }
                    }
                }
            }
            is NowPlayingUiState.Idle -> {
                if (navController.currentDestination?.route != Screen.Idle.route) {
                    navController.navigate(Screen.Idle.route) {
                        popUpTo(Screen.NowPlaying.route) { inclusive = true }
                    }
                }
            }
            is NowPlayingUiState.PermissionRequired -> {
                if (navController.currentDestination?.route != Screen.Permission.route) {
                    navController.navigate(Screen.Permission.route) {
                        popUpTo(Screen.Idle.route) { inclusive = true }
                    }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideInHorizontally { it } + fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable(Screen.Permission.route) {
            PermissionScreen(
                onPermissionGranted = {
                    navController.navigate(Screen.Idle.route) {
                        popUpTo(Screen.Permission.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Idle.route) {
            IdleScreen()
        }

        composable(Screen.NowPlaying.route) {
            NowPlayingScreen(
                viewModel = viewModel
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

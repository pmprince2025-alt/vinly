package com.vinyl.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vinyl.app.ui.idle.IdleScreen
import com.vinyl.app.ui.nowplaying.NowPlayingScreen
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
    startDestination: String = Screen.Permission.route
) {
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
            NowPlayingScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

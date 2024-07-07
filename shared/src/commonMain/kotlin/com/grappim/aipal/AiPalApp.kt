package com.grappim.aipal

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grappim.aipal.core.nav.NavDestinations
import com.grappim.aipal.feature.chat.ChatRoute
import com.grappim.aipal.feature.chat.ChatViewModel
import com.grappim.aipal.feature.settings.SettingsRoute
import com.grappim.aipal.uikit.AiPalTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun AiPalApp(
     navController: NavHostController = rememberNavController()
) {
    AiPalTheme {
        NavHost(
            navController = navController,
            startDestination = NavDestinations.Chat.route
        ) {
            composable(NavDestinations.Chat.route) {
                ChatRoute(
                    goToSettings = {
                        navController.navigate(NavDestinations.Settings.route)
                    },
                    goToApiKeySetup = {
                        navController.navigate(NavDestinations.ApiKeys.route)
                    }
                )
            }
            composable(NavDestinations.Settings.route) {
                SettingsRoute(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(NavDestinations.Prompts.route) {
                Text(text = "Prompts")
            }
            composable(NavDestinations.ApiKeys.route) {
                Text(text = "ApiKeys")
            }
            composable(NavDestinations.AiSettings.route) {
                Text(text = "AiSettings")
            }
            composable(NavDestinations.SttSettings.route) {
                Text(text = "SttSettings")
            }
        }
    }
}

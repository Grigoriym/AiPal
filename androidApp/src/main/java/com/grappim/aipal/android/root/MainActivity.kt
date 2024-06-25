package com.grappim.aipal.android.root

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grappim.aipal.android.data.local.DarkThemeConfig
import com.grappim.aipal.android.feature.chat.ChatRoute
import com.grappim.aipal.android.feature.prompts.PromptsRoute
import com.grappim.aipal.android.feature.settings.SettingsRoute
import com.grappim.aipal.android.feature.settings.ai.AiSettingsRoute
import com.grappim.aipal.android.feature.settings.apiKeys.GptSettingsRoute
import com.grappim.aipal.android.nav.NavDestinations
import com.grappim.aipal.android.uikit.MyApplicationTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val state by viewModel.state.collectAsState()
            val darkTheme = shouldUseDarkTheme(mainActivityViewState = state)

            MyApplicationTheme(
                darkTheme = darkTheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()
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
                            SettingsRoute(onBack = {
                                navController.popBackStack()
                            }, goToPrompts = {
                                navController.navigate(NavDestinations.Prompts.route)
                            }, goToApiKeysSettings = {
                                navController.navigate(NavDestinations.ApiKeys.route)
                            }, goToAiSettings = {
                                navController.navigate(NavDestinations.AiSettings.route)
                            })
                        }
                        composable(NavDestinations.Prompts.route) {
                            PromptsRoute(
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(NavDestinations.ApiKeys.route) {
                            GptSettingsRoute(onBack = {
                                navController.popBackStack()
                            })
                        }
                        composable(NavDestinations.AiSettings.route) {
                            AiSettingsRoute(onBack = {
                                navController.popBackStack()
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun shouldUseDarkTheme(mainActivityViewState: MainState): Boolean =
    when (mainActivityViewState.darkThemeConfig) {
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
    }

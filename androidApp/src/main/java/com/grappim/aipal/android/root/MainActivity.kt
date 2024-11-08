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
import com.grappim.aipal.android.feature.chat.ChatRoute
import com.grappim.aipal.android.feature.settings.SettingsRoute
import com.grappim.aipal.android.feature.settings.ai.AiSettingsRoute
import com.grappim.aipal.android.feature.settings.apiKeys.GptSettingsRoute
import com.grappim.aipal.android.feature.stt.SttSettingsRoute
import com.grappim.aipal.core.nav.NavDestinations
import com.grappim.aipal.data.model.DarkThemeConfig
import com.grappim.aipal.feature.main.MainState
import com.grappim.aipal.feature.prompts.PromptsRoute
import com.grappim.aipal.feature.settings.language.LanguageRoute
import com.grappim.aipal.uikit.AiPalTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val state by viewModel.state.collectAsState()
            val darkTheme = shouldUseDarkTheme(mainActivityViewState = state)

            AiPalTheme(
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
                            }, goToSstSettings = {
                                navController.navigate(NavDestinations.SttSettings.route)
                            }, goToLanguageSettings = {
                                navController.navigate(NavDestinations.LanguageSettings.route)
                            })
                        }
                        composable(NavDestinations.Prompts.route) {
                            PromptsRoute(
                                viewModel = koinViewModel(),
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
                        composable(NavDestinations.SttSettings.route) {
                            SttSettingsRoute(
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(NavDestinations.LanguageSettings.route) {
                            LanguageRoute(
                                viewModel = koinViewModel(),
                                onBack = {
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

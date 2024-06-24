package com.grappim.aipal.android.root

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grappim.aipal.android.feature.chat.ChatRoute
import com.grappim.aipal.android.feature.settings.SettingsRoute
import com.grappim.aipal.android.nav.NavDestinations
import com.grappim.aipal.android.uikit.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MyApplicationTheme {
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
                                }
                            )
                        }
                        composable(NavDestinations.Settings.route) {
                            SettingsRoute(onBack = {
                                navController.popBackStack()
                            })
                        }
                    }
                }
            }
        }
    }
}

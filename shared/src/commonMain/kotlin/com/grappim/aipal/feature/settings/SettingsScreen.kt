package com.grappim.aipal.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.grappim.aipal.widgets.PlatoTopBar

class SettingsScreen : Screen {
    @Composable
    override fun Content() {
        Scaffold(
            topBar = {
                PlatoTopBar(onBack = {}, title = "Settings")
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {

            }
        }
    }
}

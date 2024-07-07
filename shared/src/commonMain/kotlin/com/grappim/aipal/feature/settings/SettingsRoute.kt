package com.grappim.aipal.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.grappim.aipal.widgets.PlatoTextButton
import com.grappim.aipal.widgets.PlatoTopBar

@Composable
fun SettingsRoute(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            PlatoTopBar(onBack = onBack, title = "Settings")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            PlatoTextButton(text = "Setup TTS", onClick = { })
            PlatoTextButton(text = "Setup STT", onClick = {})
            PlatoTextButton(text = "Setup Prompts", onClick = {})
            PlatoTextButton(text = "Api Keys", onClick = {})

            Spacer(modifier = Modifier.height(12.dp))

            PlatoTextButton(text = "AI Settings", onClick = {})
            PlatoTextButton(text = "UI Settings", onClick = { })
        }
    }
}

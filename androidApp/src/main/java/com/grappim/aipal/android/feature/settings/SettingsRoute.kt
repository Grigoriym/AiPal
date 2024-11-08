package com.grappim.aipal.android.feature.settings

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.grappim.aipal.android.feature.settings.ui.UiOptionDialog
import com.grappim.aipal.widgets.PlatoTextButton
import com.grappim.aipal.widgets.PlatoTopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = koinViewModel(),
    onBack: () -> Unit,
    goToPrompts: () -> Unit,
    goToApiKeysSettings: () -> Unit,
    goToAiSettings: () -> Unit,
    goToSstSettings: () -> Unit,
    goToLanguageSettings: () -> Unit
) {
    val context = LocalContext.current
    val ttsIntent by remember { mutableStateOf(Intent("com.android.settings.TTS_SETTINGS")) }
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            PlatoTopBar(onBack = onBack, title = "Settings")
        },
    ) { paddingValues ->
        UiOptionDialog(state = state, onDismissed = { state.onShowUiSettings(false) })

        Column(
            modifier =
            Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            PlatoTextButton(text = "Setup TTS", onClick = { context.startActivity(ttsIntent) })
            PlatoTextButton(text = "Setup STT", onClick = goToSstSettings)
            PlatoTextButton(text = "Setup Prompts", onClick = goToPrompts)
            PlatoTextButton(text = "Api Keys", onClick = goToApiKeysSettings)

            Spacer(modifier = Modifier.height(12.dp))

            PlatoTextButton(text = "AI Settings", onClick = goToAiSettings)
            PlatoTextButton(text = "UI Settings", onClick = { state.onShowUiSettings(true) })
            PlatoTextButton(text = "Language Settings", onClick = goToLanguageSettings)
        }
    }
}

package com.grappim.aipal.android.feature.settings

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.grappim.aipal.android.feature.settings.ai.AiOptionsDialog
import com.grappim.aipal.android.feature.settings.ui.UiOptionDialog
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = koinViewModel(),
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val ttsIntent by remember { mutableStateOf(Intent("com.android.settings.TTS_SETTINGS")) }
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        AiOptionsDialog(
            onDismissed = { viewModel.showAiSettings(false) },
            state = state,
        )
        UiOptionDialog(state = state, onDismissed = { state.onShowUiSettings(false) })

        Column(
            modifier =
            Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Button(onClick = { context.startActivity(ttsIntent) }) {
                Text(text = "Setup TTS")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.behavior,
                onValueChange = state.onBehaviorValueChange,
                label = { Text("Behavior") },
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = state.onSetBehavior) {
                Text(text = "Set behavior")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = { viewModel.showAiSettings(true) }) {
                Text(text = "AI Settings")
            }
            Button(onClick = { state.onShowUiSettings(true)}) {
                Text(text = "UI Settings")
            }
        }
    }
}

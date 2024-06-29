package com.grappim.aipal.android.feature.stt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.grappim.aipal.android.feature.settings.ai.DynamicSelectTextField
import com.grappim.aipal.data.recognition.CurrentSTTManager
import com.grappim.aipal.widgets.PlatoTopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun SttSettingsRoute(
    viewModel: SttSettingsViewModel = koinViewModel(),
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            PlatoTopBar(title = "STT settings", onBack = onBack)
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
        ) {
            LanguageChooser(state)
            SstManagerChooser(state)
            VoskLanguageModelChooser(state)
        }
    }
}

@Composable
private fun VoskLanguageModelChooser(state: SttSettingsState) {
    if (state.currentSTTManager == CurrentSTTManager.Vosk) {
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun LanguageChooser(state: SttSettingsState) {
    Text(text = "Here you can choose the language to work with")
    Spacer(modifier = Modifier.height(6.dp))

    DynamicSelectTextField(
        selectedValue = state.currentLanguage.title,
        options = state.languages,
        label = "Language",
        onValueChangedEvent = state.onSetCurrentLanguage,
    )
}

@Composable
private fun SstManagerChooser(state: SttSettingsState) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = "Here you can choose the Sst engine to work with")
    Spacer(modifier = Modifier.height(6.dp))
    Column {
        CurrentSTTManager.entries.forEach { sst ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (sst == state.currentSTTManager),
                        onClick = {
                            state.onSstManagerChange(sst)
                        },
                    ).padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = (sst == state.currentSTTManager),
                    onClick = null,
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = sst.name,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = sst.description,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

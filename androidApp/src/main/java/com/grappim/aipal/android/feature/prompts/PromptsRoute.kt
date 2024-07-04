package com.grappim.aipal.android.feature.prompts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.grappim.aipal.widgets.PlatoTopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun PromptsRoute(viewModel: PromptsViewModel = koinViewModel(), onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            PlatoTopBar(onBack = onBack, title = "Prompts")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PromptElementContent(
                title = "Behavior sets the initial setup for the AI, i.e. context",
                value = state.behavior,
                onValueChange = state.onSetBehavior,
                label = "Behavior prompt",
                onSave = state.saveBehavior,
                saveButtonText = "Save Behavior"
            )
            Spacer(modifier = Modifier.height(8.dp))
            PromptElementContent(
                title = "Translation Prompt sets the message which is sent upon translating the text",
                value = state.translationPrompt,
                onValueChange = state.onSetTranslationPrompt,
                label = "Translation prompt",
                onSave = state.saveTranslationPrompt,
                saveButtonText = "Save Translation Prompt"
            )
            Spacer(modifier = Modifier.height(8.dp))
            PromptElementContent(
                title = "Spelling Check Prompt sets the message which is sent upon checking the spelling",
                value = state.spellingCheckPrompt,
                onValueChange = state.onSetSpelling,
                label = "Spelling check prompt",
                onSave = state.saveSpelling,
                saveButtonText = "Save Spelling check prompt"
            )
            Spacer(modifier = Modifier.height(8.dp))
            PromptElementContent(
                title = "A Prompt for removing some symbols from AI answer so that TTS " +
                        "engine wouldn't pronounce them",
                value = state.aiAnswerFixPrompt,
                onValueChange = state.onSetAiAnswerFixPrompt,
                label = "Ai fix Prompt",
                onSave = state.saveAiAnswerFixPrompt,
                saveButtonText = "Save Ai fix prompt"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PromptElementContent(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    onSave: () -> Unit,
    saveButtonText: String
) {
    Card(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(text = title)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onSave) {
                Text(text = saveButtonText)
            }
        }
    }
}

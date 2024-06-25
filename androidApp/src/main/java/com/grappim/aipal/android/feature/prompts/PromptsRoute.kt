package com.grappim.aipal.android.feature.prompts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.grappim.aipal.android.uikit.PlatoTopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun PromptsRoute(viewModel: PromptsViewModel = koinViewModel(), onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            PlatoTopBar(onBack = onBack, title = "Prompts")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Behavior sets the initial setup for the AI, i.e. context")
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.behavior,
                onValueChange = state.onSetBehavior,
                label = { Text("Behavior") },
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = state.saveBehavior) {
                Text(text = "Set behavior")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Translation Prompt sets the message when we want to translate a message")
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.translationPrompt,
                onValueChange = state.onSetTranslationPrompt,
                label = { Text("TranslationPrompt") },
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = state.saveTranslationPrompt) {
                Text(text = "Set Translation Prompt")
            }
        }
    }
}

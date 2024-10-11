package com.grappim.aipal.feature.prompts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
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

@Composable
fun PromptsRoute(viewModel: PromptsViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            PlatoTopBar(onBack = onBack, title = "Prompts")
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(state.promptElements) { index, element ->
                PromptElementContent(
                    title = element.title,
                    value = element.value,
                    onValueChange = { value ->
                        state.onUpdatePromptElement(index, value)
                    },
                    label = element.label,
                    saveButtonText = element.saveButtonText,
                    onSave = {
                        element.onSave(index)
                    },
                )
            }
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
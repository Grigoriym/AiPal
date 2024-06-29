package com.grappim.aipal.android.feature.settings.ai

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.grappim.aipal.widgets.PlatoTopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun AiSettingsRoute(
    onBack: () -> Unit,
    viewModel: AiSettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        modifier =
        Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        topBar = {
            PlatoTopBar(onBack = onBack, title = "Ai Settings")
        },
    ) { padding ->
        Column(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier =
                Modifier.fillMaxWidth(),
                text = "Temperature: ${state.tempValue}",
                textAlign = TextAlign.Center,
            )
            Slider(
                value = state.tempValue.toFloat(),
                onValueChange = {
                    state.onSetTempValue(it.toDouble())
                },
                valueRange = 0f..2f,
            )

            Button(onClick = {
                state.onGetModels()
            }) {
                Text(text = "Fetch models")
            }

            Spacer(modifier = Modifier.height(12.dp))

            DynamicSelectTextField(
                modifier = Modifier.fillMaxWidth(),
                selectedValue = state.selectedModel,
                options = state.models,
                label = "Model",
                onValueChangedEvent = state.onSetModel,
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = state.applySettings) {
                Text(text = "Apply")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicSelectTextField(
    modifier: Modifier = Modifier,
    selectedValue: String,
    options: Set<String>,
    label: String,
    onValueChangedEvent: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier,
    ) {
        OutlinedTextField(
            modifier =
            Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            value = selectedValue,
            onValueChange = {},
            label = { Text(text = label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(),
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option: String ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        expanded = false
                        onValueChangedEvent(option)
                    },
                )
            }
        }
    }
}

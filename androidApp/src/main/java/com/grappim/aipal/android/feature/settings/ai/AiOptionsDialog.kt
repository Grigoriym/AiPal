package com.grappim.aipal.android.feature.settings.ai

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.grappim.aipal.android.feature.settings.SettingsState

@Composable
fun AiOptionsDialog(
    onDismissed: () -> Unit,
    state: SettingsState,
) {
    if (state.showAiSettings) {
        Dialog(onDismissRequest = onDismissed) {
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
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
                        onValueChangedEvent = { value ->
                            state.onSetModel(value)
                        },
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(onClick = state.applySettings) {
                        Text(text = "Apply")
                    }
                }
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

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.grappim.aipal.widgets.PlatoSelectTextField
import com.grappim.aipal.widgets.PlatoTopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun AiSettingsRoute(
    onBack: () -> Unit,
    viewModel: AiSettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostSate = remember {
        SnackbarHostState()
    }
    LaunchedEffect(state.snackbarMessage) {
        if (state.snackbarMessage.data.isNotEmpty()) {
            val result = snackbarHostSate.showSnackbar(message = state.snackbarMessage.data)
            if (result == SnackbarResult.ActionPerformed) {
                snackbarHostSate.currentSnackbarData?.dismiss()
            }
        }
    }
    Scaffold(
        modifier =
        Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        topBar = {
            PlatoTopBar(onBack = onBack, title = "Ai Settings")
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostSate) }
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

            Button(onClick = state.applyTemp) {
                Text(text = "Apply Temperature")
            }

            Spacer(modifier = Modifier.height(12.dp))

            PlatoSelectTextField(
                modifier = Modifier.fillMaxWidth(),
                selectedValue = state.selectedModel,
                options = state.models,
                label = "Model",
                onValueChangedEvent = state.onSetModel,
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = {
                state.onGetModels()
            }) {
                Text(text = "Fetch models")
            }
        }
    }
}

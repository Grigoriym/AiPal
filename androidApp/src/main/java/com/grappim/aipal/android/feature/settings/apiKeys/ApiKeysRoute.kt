package com.grappim.aipal.android.feature.settings.apiKeys

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.grappim.aipal.widgets.PlatoTopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun GptSettingsRoute(onBack: () -> Unit, viewModel: ApiKeysViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val snackbarHostSate = remember {
        SnackbarHostState()
    }
    LaunchedEffect(state.snackbarMessage) {
        if (state.snackbarMessage.data.isNotEmpty()) {
            val result = snackbarHostSate.showSnackbar(message = state.snackbarMessage.data)
            if (result == SnackbarResult.ActionPerformed) {
                snackbarHostSate.currentSnackbarData?.dismiss()
                state.dismissSnackbar()
            }
        }
    }
    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        topBar = {
            PlatoTopBar(onBack = onBack, title = "Api Keys")
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostSate) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
            Text(text = "OpenAi Api Key")
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                value = state.openAiApiKey,
                onValueChange = state.onSetOpenAiApiKey,
                label = { Text("Api Key") },
                trailingIcon = {
                    IconButton(onClick = state.onKeyClear) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Backspace, contentDescription = "")
                    }
                }
            )
            Spacer(modifier = Modifier.height(6.dp))
            Button(onClick = state.saveOpenAiApiKey) {
                Text(text = "Save Api Key")
            }
            Button(onClick = state.onCheckApiKey) {
                Text(text = "Check Api Key")
            }
        }
    }
}

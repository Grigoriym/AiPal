package com.grappim.aipal.android.feature.stt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grappim.aipal.data.recognition.CurrentSTTManager
import com.grappim.aipal.data.recognition.ModelRetrievalState
import com.grappim.aipal.widgets.PlatoAlertDialog
import com.grappim.aipal.widgets.PlatoTopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun SttSettingsRoute(
    viewModel: SttSettingsViewModel = koinViewModel(),
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostSate = remember {
        SnackbarHostState()
    }

    LaunchedEffect(state.snackbarMessage) {
        if (state.snackbarMessage.data.message.isNotEmpty()) {
            val result = snackbarHostSate.showSnackbar(
                message = state.snackbarMessage.data.message,
                duration = SnackbarDuration.Short
            )
            state.acknowledgeError()
            if (result == SnackbarResult.ActionPerformed) {
                snackbarHostSate.currentSnackbarData?.dismiss()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostSate) },
        topBar = {
            PlatoTopBar(title = "STT settings", onBack = {
                state.acknowledgeError()
                onBack()
            })
        },
    ) { padding ->
        PlatoAlertDialog(
            text = "Please wait. The STT model is being downloaded and prepared.",
            showAlertDialog = state.showAlertDialog,
            onDismissRequest = state.onDismissDialog
        )

        Column(
            modifier =
            Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        ) {
            SstManagerChooser(state)
            VoskLanguageModelChooser(state)
        }
    }
}

@Composable
private fun VoskLanguageModelChooser(state: SttSettingsState) {
    if (state.currentSTTManager == CurrentSTTManager.Vosk) {
        val uriHandler = LocalUriHandler.current

        Spacer(modifier = Modifier.height(16.dp))
        val annotatedString = buildAnnotatedString {
            append("You can check which models are available for Vosk")
            pushStringAnnotation(tag = "link", annotation = "https://alphacephei.com/vosk/models")
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append(" here")
            }
            pop()
        }
        ClickableText(text = annotatedString, onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "link", start = offset, end = offset)
                .firstOrNull()?.let { stringAnnotation ->
                    uriHandler.openUri(stringAnnotation.item)
                }
        })
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Currently available models")

        LazyColumn {
            items(state.availableModels) { ui ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                ) {
                    val model = ui.voskModelAvailability
                    Column(
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = model.supportedLanguage.title)

                            IconButton(
                                enabled = !model.isAvailable && !state.isDownloading,
                                onClick = {
                                    state.onModelDownload(model)
                                }) {
                                Icon(
                                    imageVector = Icons.Filled.Download,
                                    contentDescription = ""
                                )
                            }
                        }

                        if (model.isAvailable) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Model is downloaded and ready to use")
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = Icons.Filled.CheckCircle.name,
                                    tint = Color.Green
                                )
                            }
                        } else {
                            VoskModelModelStateContent(ui)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VoskModelModelStateContent(ui: VoskModelUI) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (ui.voskModelUIState) {
            is ModelRetrievalState.Downloading -> {
                Text(text = "Model Downloading: ${ui.voskModelUIState.progress} %")
            }

            is ModelRetrievalState.Initial, is ModelRetrievalState.Error -> {
                Text(text = "Model is not downloaded")
            }

            else -> {
                Text(text = "Model Downloaded")
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = Icons.Filled.CheckCircle.name,
                    tint = Color.Green
                )
            }
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (ui.voskModelUIState) {
            is ModelRetrievalState.Unzipping -> {
                Text(text = "Model Unzipping: ${ui.voskModelUIState.progress} %")
            }

            is ModelRetrievalState.Initial, is ModelRetrievalState.Error -> {

            }

            else -> {
                Text(text = "Model Unzipping: 0 % ")
            }
        }
    }
}

@Composable
private fun SstManagerChooser(state: SttSettingsState) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = "Here you can choose the STT engine")
    Spacer(modifier = Modifier.height(6.dp))
    Column {
        CurrentSTTManager.entries.forEach { sst ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (sst == state.currentSTTManager),
                        onClick = {
                            state.onSttManagerChange(sst)
                        },
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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

package com.grappim.aipal.android.feature.chat

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import nl.marc_apps.tts.TextToSpeechEngine
import nl.marc_apps.tts.rememberTextToSpeechOrNull
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatRoute(
    viewModel: ChatViewModel = koinViewModel(),
    goToSettings: () -> Unit,
    goToApiKeySetup: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val permissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)
    val textToSpeech = rememberTextToSpeechOrNull(TextToSpeechEngine.SystemDefault)
    val coroutineScope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val listState = rememberLazyListState()
    val snackbarHostSate = remember {
        SnackbarHostState()
    }

    if (!permissionState.status.isGranted) {
        LaunchedEffect(true) {
            permissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(state.snackbarMessage) {
        if (state.snackbarMessage.data.message.isNotEmpty()) {
            val result = snackbarHostSate.showSnackbar(
                message = state.snackbarMessage.data.message,
                actionLabel = if (state.snackbarMessage.data.goToApiKeysScreen) "Set up Key" else null
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    goToApiKeySetup()
                    state.dismissSnackbar()
                }

                SnackbarResult.Dismissed -> {
                    snackbarHostSate.currentSnackbarData?.dismiss()
                }
            }
        }
    }

    LaunchedEffect(state.assistantMessage) {
        if (state.assistantMessage.isNotEmpty()) {
            runCatching {
                textToSpeech?.stop()
                textToSpeech?.say(state.assistantMessage)
            }
        }
    }
    LaunchedEffect(state.listMessages) {
        if (state.listMessages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(index = state.listMessages.lastIndex)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostSate) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Chat") },
                actions = {
                    IconButton(
                        onClick = {
                            keyboard?.hide()
                            goToSettings()
                        },
                    ) {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = "")
                    }
//                    IconButton(
//                        onClick = {
//
//                        },
//                    ) {
//                        Icon(imageVector = Icons.Filled.History, contentDescription = "")
//                    }
                },
            )
        },
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        bottomBar = {
            ChatBox(
                modifier =
                Modifier
                    .fillMaxWidth(),
                state = state,
                viewModel = viewModel,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
            Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
            state = listState,
        ) {
            items(state.getMessagesForUi()) { item ->
                ChatItem(
                    message = item,
                    onRepeat = {
                        coroutineScope.launch {
                            runCatching {
                                textToSpeech?.stop()
                                textToSpeech?.say(it)
                            }
                        }
                    },
                    onMessageCopy = { text ->
                        clipboardManager.setText(AnnotatedString(text))
                    },
                    onTranslate = { chatMessage ->
                        viewModel.translateMessage(chatMessage)
                    },
                )
            }
        }
    }
}

@Composable
private fun ChatItem(
    message: ChatMessageUI,
    onRepeat: (String) -> Unit,
    onMessageCopy: (String) -> Unit,
    onTranslate: (ChatMessageUI) -> Unit,
) {
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 6.dp),
        horizontalArrangement = if (message.isUserMessage) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!message.isUserMessage) {
            IconButton(onClick = { onRepeat(message.message) }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "")
            }
            IconButton(onClick = { onMessageCopy(message.message) }) {
                Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = "")
            }
            IconButton(onClick = { onTranslate(message) }) {
                Icon(imageVector = Icons.Filled.Translate, contentDescription = "")
            }
        }
        Card(
            shape = RoundedCornerShape(
                topStart = 24f,
                topEnd = 24f,
                bottomStart = if (message.isUserMessage) 24f else 0f,
                bottomEnd = if (message.isUserMessage) 0f else 24f,
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = message.message)
                if (message.translation.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(color = Color.Blue, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = message.translation)
                }
            }
        }
        if (message.isUserMessage) {
            IconButton(onClick = { onMessageCopy(message.message) }) {
                Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = "")
            }
        }
    }
}

@Composable
private fun ChatBox(
    modifier: Modifier = Modifier,
    state: ChatState,
    viewModel: ChatViewModel,
) {
    Row(
        modifier = modifier.padding(16.dp),
    ) {
        TextField(
            modifier = Modifier.weight(1f),
            value = state.clientMessage,
            onValueChange = state.onEditMessage,
            placeholder = { Text("Message...") },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            trailingIcon = {
                IconButton(onClick = state.onMessageClear) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Backspace, contentDescription = "")
                }
            }
        )
        IconButton(
            modifier =
            Modifier
                .clip(CircleShape)
                .align(Alignment.CenterVertically),
            onClick = state.toggleSTT,
        ) {
            Icon(imageVector = state.fabIcon, contentDescription = "")
        }
        IconButton(
            modifier =
            Modifier
                .clip(CircleShape)
                .align(Alignment.CenterVertically),
            onClick = { viewModel.sendMessage() },
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "")
        }
    }
}

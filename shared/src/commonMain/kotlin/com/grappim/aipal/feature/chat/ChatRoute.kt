package com.grappim.aipal.feature.chat

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grappim.aipal.widgets.PlatoIconButton
import com.grappim.aipal.widgets.PlatoTopBar
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun ChatRoute(
    chatViewModel: ChatViewModel = koinViewModel(),
    goToSettings: () -> Unit,
    goToApiKeySetup: () -> Unit,
) {
    val state by chatViewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val keyboard = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    Scaffold(
        modifier = Modifier.statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        topBar = {
            PlatoTopBar(
                title = "Chat",
                actions = {
                    PlatoIconButton(
                        icon = Icons.Filled.Pause,
                        onButtonClick = {

                        })
                    PlatoIconButton(
                        icon = Icons.Filled.Settings,
                        onButtonClick = {
                            keyboard?.hide()
                            goToSettings()
                        })
                })
        },
        bottomBar = {
            ChatBox(modifier = Modifier.fillMaxWidth(), state = state)
        }
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
//                        coroutineScope.launch {
//                            runCatching {
//                                textToSpeech?.stop()
//                                textToSpeech?.say(it)
//                            }
//                        }
                    },
                    onMessageCopy = { text ->
                        clipboardManager.setText(AnnotatedString(text))
                    },
                    onTranslate = state.onTranslateMessage,
                    onSpellCheck = { chatMessage ->
                        state.onSpellCheck(chatMessage)
                    }
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
    onSpellCheck: (ChatMessageUI) -> Unit,
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
            modifier = Modifier.weight(1f),
            shape =
            RoundedCornerShape(
                topStart = 24f,
                topEnd = 24f,
                bottomStart = if (message.isUserMessage) 24f else 0f,
                bottomEnd = if (message.isUserMessage) 0f else 24f,
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(text = message.message)
                if (message.spellingCheck.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(color = Color.Green, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = message.spellingCheck)
                }
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
            IconButton(onClick = { onSpellCheck(message) }) {
                Icon(imageVector = Icons.Filled.Spellcheck, contentDescription = "")
            }
        }
    }
}

@Composable
private fun ChatBox(
    modifier: Modifier = Modifier,
    state: ChatState
) {
    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier.weight(1f),
            value = state.clientMessage,
            onValueChange = state.onEditClientMessage,
            placeholder = { Text("Message...") },
            shape = RoundedCornerShape(24.dp),
            colors =
            TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            trailingIcon = {
                PlatoIconButton(
                    icon = Icons.AutoMirrored.Filled.Backspace,
                    onButtonClick = state.onMessageClear
                )
            },
        )

        AnimatedMicrophoneButton(
            modifier = Modifier
                .padding(start = 8.dp),
            state = state,
//            permissionState = permissionState
        )

        PlatoIconButton(
            icon = Icons.AutoMirrored.Filled.Send,
            onButtonClick = state.onSendMessage,
            enabled = state.clientMessage.isNotEmpty(),
        )
    }
}

@Composable
private fun AnimatedMicrophoneButton(
    modifier: Modifier = Modifier,
    state: ChatState,
//    permissionState: PermissionState
) {
    val color by animateColorAsState(
        targetValue = when {
            state.isDownloading -> Color(0xFF039BCF)
            state.isListening -> Color.Green
            state.isPreparingModel -> Color(0xFFFFA500)
            else -> LocalContentColor.current
        }, label = "mic color"
    )
    val scale by animateFloatAsState(
        targetValue = if (state.isPreparingModel || state.isDownloading) 1.2f else 1f, label = ""
    )

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val borderWidth by infiniteTransition.animateFloat(
        initialValue = 2f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ), label = ""
    )

    Box(
        modifier = modifier
            .size(40.dp)
            .scale(scale)
    ) {
        if (state.isListening || state.isPreparingModel || state.isDownloading) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                rotate(if (state.isPreparingModel) rotationAngle else 0f) {
                    drawCircle(
                        color = color,
                        style = Stroke(width = borderWidth.dp.toPx()),
                        radius = size.minDimension / 2
                    )
                }
            }
        }

//        PlatoIconButton(
//            icon = state.fabIcon,
//            onButtonClick = {
//                if (permissionState.status.isGranted) {
//                    state.toggleSTT()
//                } else {
//                    val textToShow = if (permissionState.status.shouldShowRationale) {
//                        "The Microphone is important for this app. Please grant the permission."
//                    } else {
//                        "Microphone permission required for this feature to be available. " +
//                                "Please grant the permission"
//                    }
//                    state.onShowPermissionsAlertDialog(true, textToShow)
//                }
//            },
//            tint = color
//        )
    }
}

package com.grappim.aipal.feature.chat

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.grappim.aipal.widgets.PlatoIconButton
import com.grappim.aipal.widgets.PlatoTopBar

@Composable
fun ChatRoute(
    goToSettings: () -> Unit,
    goToApiKeySetup: () -> Unit,
) {
    val listState = rememberLazyListState()
    val keyboard = LocalSoftwareKeyboardController.current

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
            ChatBox(modifier = Modifier.fillMaxWidth())
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding)
                .fillMaxWidth(),
            state = listState
        ) {

        }
    }
}

@Composable
private fun ChatBox(
    modifier: Modifier = Modifier,
//    state: ChatState
) {
    Row(
        modifier = modifier.padding(16.dp),
    ) {
        TextField(
            modifier = Modifier.weight(1f),
            value = "state.clientMessage",
            onValueChange = { },
            placeholder = { Text("Message...") },
            shape = RoundedCornerShape(24.dp),
            colors =
            TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            trailingIcon = {
//                IconButton(onClick = state.onMessageClear) {
//                    Icon(imageVector = Icons.AutoMirrored.Filled.Backspace, contentDescription = "")
//                }
            },
        )
        IconButton(
            modifier =
            Modifier
                .clip(CircleShape)
                .align(Alignment.CenterVertically),
            onClick = {},
        ) {
//            Icon(imageVector = state.fabIcon, contentDescription = "")
        }
        IconButton(
            modifier =
            Modifier
                .clip(CircleShape)
                .align(Alignment.CenterVertically),
            onClick = { },
//            enabled = state.clientMessage.isNotEmpty()
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "")
        }
    }
}
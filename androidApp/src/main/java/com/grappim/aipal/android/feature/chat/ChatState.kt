package com.grappim.aipal.android.feature.chat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.ui.graphics.vector.ImageVector

data class ChatState(
    val listeningState: ListeningState = ListeningState.Idle,
    val clientMessage: String = "",
    val assistantMessage: String = "",
    val models: List<String> = emptyList(),
    val isSynthesizing: Boolean = false,
    val fabIcon: ImageVector = Icons.Filled.Mic,
    val listMessages: List<ChatMessageUI> = emptyList()
) {
    fun getMessagesForUi() = listMessages.filter { it.message.isNotEmpty() }
}

sealed interface ListeningState {
    data object Idle : ListeningState

    data object Listening : ListeningState
}

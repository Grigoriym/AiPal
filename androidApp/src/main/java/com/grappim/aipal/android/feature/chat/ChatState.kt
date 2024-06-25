package com.grappim.aipal.android.feature.chat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.ui.graphics.vector.ImageVector
import com.grappim.aipal.android.core.LaunchedEffectResult

data class ChatState(
    val listeningState: ListeningState = ListeningState.Idle,
    val clientMessage: String = "",
    val assistantMessage: String = "",
    val models: List<String> = emptyList(),
    val isSynthesizing: Boolean = false,
    val fabIcon: ImageVector = Icons.Filled.Mic,
    val listMessages: List<ChatMessageUI> = emptyList(),
    val onMessageClear: () -> Unit,
    val onEditClientMessage: (String) -> Unit,
    val toggleSTT: () -> Unit,
    val snackbarMessage: LaunchedEffectResult<SnackbarData> = LaunchedEffectResult(SnackbarData()),
    val dismissSnackbar: () -> Unit
) {
    fun getMessagesForUi() = listMessages.filter { it.message.isNotEmpty() }
}

data class SnackbarData(
    val message: String = "",
    val goToApiKeysScreen: Boolean = false
)

sealed interface ListeningState {
    data object Idle : ListeningState

    data object Listening : ListeningState
}

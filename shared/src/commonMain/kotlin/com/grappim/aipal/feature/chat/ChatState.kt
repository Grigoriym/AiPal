package com.grappim.aipal.feature.chat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.ui.graphics.vector.ImageVector
import com.grappim.aipal.core.LaunchedEffectResult

data class ChatState(
    val listeningState: ListeningState = ListeningState.Idle,
    val clientMessage: String = "",
    val assistantMessage: String = "",
    val models: List<String> = emptyList(),
    val fabIcon: ImageVector = Icons.Filled.Mic,
    val listMessages: List<ChatMessageUI> = emptyList(),
    val onMessageClear: () -> Unit,
    val onEditClientMessage: (String) -> Unit,
    val toggleSTT: () -> Unit,
    val snackbarMessage: LaunchedEffectResult<SnackbarData> = LaunchedEffectResult(SnackbarData()),
    val dismissSnackbar: () -> Unit,
    val isListening: Boolean = false,
    val isPreparingModel: Boolean = false,
    val isDownloading: Boolean = false,
    val showAlertDialog: Boolean = false,
    val onDismissDialog: () -> Unit,
    val acknowledgeError: () -> Unit
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
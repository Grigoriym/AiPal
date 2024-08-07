package com.grappim.aipal.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.core.LaunchedEffectResult
import com.grappim.aipal.data.exceptions.OpenAiEmptyApiKeyException
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.recognition.STTFactory
import com.grappim.aipal.data.recognition.STTManager
import com.grappim.aipal.data.repo.AiPalRepo
import com.grappim.aipal.data.uuid.UuidGenerator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging

class ChatViewModel(
    private val aiPalRepo: AiPalRepo,
    private val localDataStorage: LocalDataStorage,
    private val uuidGenerator: UuidGenerator,
    private val sttFactory: STTFactory
) : ViewModel() {
    private lateinit var sttManager: STTManager

    private var runningJob: Job? = null

    private val _state =
        MutableStateFlow(
            ChatState(
                onMessageClear = ::onMessageClear,
                onEditClientMessage = ::editResultMessage,
                toggleSTT = ::toggleSTT,
                dismissSnackbar = ::dismissSnackbar,
                onDismissDialog = ::dismissDialog,
                acknowledgeError = ::acknowledgeError,
                onSpellCheck = ::checkSpelling,
                onShowPermissionsAlertDialog = ::onShowPermissionsAlertDialog,
                onSendMessage = ::sendMessage,
                onTranslateMessage = ::translateMessage,
                onMessageRefresh = ::onMessageRefresh
            ),
        )
    val state = _state.asStateFlow()

    private val logging = logging()

    init {

    }

    private fun onMessageRefresh(chatMessageUI: ChatMessageUI) {

    }

    private fun onMessageClear() {
        editResultMessage("")
    }

    private fun editResultMessage(newMsg: String) {
        viewModelScope.launch {
            _state.update { it.copy(clientMessage = newMsg) }
        }
    }

    private fun dismissDialog() {
        _state.update { it.copy(showAlertDialog = true) }
    }

    private fun dismissSnackbar() {
        _state.update { it.copy(snackbarMessage = LaunchedEffectResult(SnackbarData())) }
    }

    private fun acknowledgeError() {
        sttManager.resetToDefaultState()
    }

    private fun checkSpelling(chatMessageUI: ChatMessageUI) {
        viewModelScope.launch {
            val result = aiPalRepo.checkSpelling(chatMessageUI.message)
            result
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            snackbarMessage =
                            LaunchedEffectResult(
                                SnackbarData(
                                    message = e.message ?: "Error, try checking api key",
                                    goToApiKeysScreen = e is OpenAiEmptyApiKeyException,
                                ),
                            ),
                        )
                    }
                }.onSuccess { value ->
                    if (value.isNotEmpty()) {
                        val newUiMessage = chatMessageUI.copy(spellingCheck = value)
                        val messages = state.value.listMessages.toMutableList()
                        val index = messages.indexOf(chatMessageUI)
                        messages[index] = newUiMessage
                        _state.update {
                            it.copy(listMessages = messages.toList())
                        }
                    }
                }
        }
    }

    private fun onShowPermissionsAlertDialog(show: Boolean, text: String?) {
        _state.update {
            it.copy(
                showProvidePermissionsAlertDialog = show,
                permissionsAlertDialogText = text ?: ""
            )
        }
    }

    private fun toggleSTT() {
        viewModelScope.launch {
            if (sttManager.state.value.isSpeaking) {
                sttManager.stopListening()
            } else {
                sttManager.startListening()
            }
        }
    }

    private fun turnOffSpeechService() {
        sttManager.stopListening()
    }

    private fun sendMessage() {
        viewModelScope.launch {
            turnOffSpeechService()
            val messageId = uuidGenerator.getUuid4()
            val msgToSend = state.value.clientMessage
            val uiMessage =
                ChatMessageUI(uuid = messageId, message = msgToSend, isUserMessage = true)
            _state.update {
                it.copy(
                    clientMessage = "",
                    listMessages = it.listMessages + uiMessage,
                )
            }
            val result = aiPalRepo.sendMessage(msgToSend, messageId)
            result
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            snackbarMessage =
                            LaunchedEffectResult(
                                SnackbarData(
                                    message = e.message ?: "Error, try checking api key",
                                    goToApiKeysScreen = e is OpenAiEmptyApiKeyException,
                                ),
                            ),
                        )
                    }
                }.onSuccess { resultMessage ->
                    val resultUiMessage = ChatMessageUI(
                        uuid = resultMessage.id,
                        message = resultMessage.text,
                        isUserMessage = false
                    )
                    _state.update {
                        it.copy(
                            assistantMessage = resultMessage.text,
                            listMessages = it.listMessages + resultUiMessage,
                        )
                    }
                }
        }
    }

    private fun translateMessage(chatMessageUI: ChatMessageUI) {
        viewModelScope.launch {
            val result = aiPalRepo.translateMessage(chatMessageUI.message)
            result
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            snackbarMessage =
                            LaunchedEffectResult(
                                SnackbarData(
                                    message = e.message ?: "Error, try checking api key",
                                    goToApiKeysScreen = e is OpenAiEmptyApiKeyException,
                                ),
                            ),
                        )
                    }
                }.onSuccess { value ->
                    if (value.isNotEmpty()) {
                        val newUiMessage = chatMessageUI.copy(translation = value)
                        val messages = state.value.listMessages.toMutableList()
                        val index = messages.indexOf(chatMessageUI)
                        messages[index] = newUiMessage
                        _state.update {
                            it.copy(listMessages = messages.toList())
                        }
                    }
                }
        }
    }
}

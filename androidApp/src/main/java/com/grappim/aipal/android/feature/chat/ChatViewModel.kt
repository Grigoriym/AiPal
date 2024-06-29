package com.grappim.aipal.android.feature.chat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.android.recognition.factory.SSTFactory
import com.grappim.aipal.core.LaunchedEffectResult
import com.grappim.aipal.data.exceptions.OpenAiEmptyApiKeyException
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.recognition.CurrentSTTManager
import com.grappim.aipal.data.recognition.STTManager
import com.grappim.aipal.data.repo.AiPalRepo
import com.grappim.aipal.feature.chat.ChatMessageUI
import com.grappim.aipal.feature.chat.ChatState
import com.grappim.aipal.feature.chat.SnackbarData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging

class ChatViewModel(
    private val aiPalRepo: AiPalRepo,
    private val localDataStorage: LocalDataStorage,
    private val sstFactory: SSTFactory,
) : ViewModel() {
    private var sstManager: STTManager = sstFactory.getSSTManager(CurrentSTTManager.default())

    private val _state =
        MutableStateFlow(
            ChatState(
                onMessageClear = ::onMessageClear,
                onEditClientMessage = ::editResultMessage,
                toggleSTT = ::toggleSTT,
                dismissSnackbar = ::dismissSnackbar,
            ),
        )
    val state = _state.asStateFlow()

    private val logging = logging()

    init {
        viewModelScope.launch {
            launch {
                localDataStorage.sttManager
                    .distinctUntilChanged()
                    .collect { value ->
                        updateSSTManager(value)
                    }
            }
        }
    }

    private fun updateSSTManager(newManger: CurrentSTTManager) {
        viewModelScope.launch {
            sstManager.cancel()
            sstManager = sstFactory.getSSTManager(newManger)

            sstManager.state.collect { value ->
                val message = state.value.clientMessage + " " + value.result
                logging.d { "here is the result: $message" }
                val fabIcon = if (value.isSpeaking) Icons.Filled.MicOff else Icons.Filled.Mic
                _state.update {
                    it.copy(
                        clientMessage = message.trim(),
                        fabIcon = fabIcon,
                        snackbarMessage = LaunchedEffectResult(SnackbarData(message = value.error)),
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        sstManager.cancel()
    }

    private fun dismissSnackbar() {
        _state.update { it.copy(snackbarMessage = LaunchedEffectResult(SnackbarData())) }
    }

    private fun onMessageClear() {
        editResultMessage("")
    }

    fun translateMessage(chatMessageUI: ChatMessageUI) {
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

    private fun editResultMessage(newMsg: String) {
        viewModelScope.launch {
            _state.update { it.copy(clientMessage = newMsg) }
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            turnOffSpeechService()
            val msgToSend = state.value.clientMessage
            val uiMessage = ChatMessageUI(message = msgToSend, isUserMessage = true)
            _state.update {
                it.copy(
                    clientMessage = "",
                    listMessages = it.listMessages + uiMessage,
                )
            }
            val result = aiPalRepo.sendMessage(msgToSend)
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
                    val resultUiMessage = ChatMessageUI(message = value, isUserMessage = false)
                    _state.update {
                        it.copy(
                            assistantMessage = value,
                            listMessages = it.listMessages + resultUiMessage,
                        )
                    }
                }
        }
    }

    private fun turnOffSpeechService() {
        sstManager.stopListening()
    }

    private fun toggleSTT() {
        viewModelScope.launch {
            if (sstManager.state.value.isSpeaking) {
                sstManager.stopListening()
            } else {
                sstManager.startListening()
            }
        }
    }
}

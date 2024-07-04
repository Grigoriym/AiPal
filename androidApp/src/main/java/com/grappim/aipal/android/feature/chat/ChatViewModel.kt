package com.grappim.aipal.android.feature.chat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.android.recognition.factory.STTFactory
import com.grappim.aipal.core.LaunchedEffectResult
import com.grappim.aipal.data.exceptions.OpenAiEmptyApiKeyException
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.recognition.CurrentSTTManager
import com.grappim.aipal.data.recognition.ModelRetrievalState
import com.grappim.aipal.data.recognition.STTManager
import com.grappim.aipal.data.repo.AiPalRepo
import com.grappim.aipal.feature.chat.ChatMessageUI
import com.grappim.aipal.feature.chat.ChatState
import com.grappim.aipal.feature.chat.SnackbarData
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.lighthousegames.logging.logging

class ChatViewModel(
    private val aiPalRepo: AiPalRepo,
    private val localDataStorage: LocalDataStorage,
    private val sttFactory: STTFactory,
) : ViewModel() {
    private var sttManager: STTManager

    private var runningJob: Job? = null

    private val _state =
        MutableStateFlow(
            ChatState(
                onMessageClear = ::onMessageClear,
                onEditClientMessage = ::editResultMessage,
                toggleSTT = ::toggleSTT,
                dismissSnackbar = ::dismissSnackbar,
                onDismissDialog = ::dismissDialog,
                acknowledgeError = ::acknowledgeError
            ),
        )
    val state = _state.asStateFlow()

    private val logging = logging()

    init {
        sttManager = runBlocking { sttFactory.getSSTManager(localDataStorage.sttManager.first()) }
        viewModelScope.launch {
            launch {
                localDataStorage.sttManager
                    .distinctUntilChanged()
                    .collect { newStt ->
                        logging.d { "received new stt: $newStt" }
                        updateSSTManager(newStt)
                    }
            }
        }
    }

    private fun acknowledgeError() {
        sttManager.resetToDefaultState()
    }

    private fun updateSSTManager(newManger: CurrentSTTManager) {
        runningJob?.cancel()
        runningJob = viewModelScope.launch {
            sttManager = sttFactory.getSSTManager(newManger)

            sttManager.state.collect { value ->
                logging.d { "value: $value" }
                logging.d { "previous message: ${state.value.clientMessage}" }
                val message = state.value.clientMessage + " " + value.result
                logging.d { "here is the result: $message" }
                val fabIcon = if (value.isSpeaking) Icons.Filled.MicOff else Icons.Filled.Mic
                _state.update {
                    it.copy(
                        isDownloading = value.modelRetrievalResult.isDownloading(),
                        isListening = value.isSpeaking,
                        isPreparingModel = value.modelRetrievalResult.modelRetrievalState is ModelRetrievalState.ModelLoading,
                        showAlertDialog = value.modelRetrievalResult.showAlertDialog(),
                        clientMessage = message.trim(),
                        fabIcon = fabIcon,
                        snackbarMessage = LaunchedEffectResult(SnackbarData(message = value.error)),
                    )
                }
            }
        }
    }

    private fun dismissDialog() {
        _state.update { it.copy(showAlertDialog = true) }
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
        sttManager.stopListening()
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
}

package com.grappim.aipal.android.feature.chat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.android.recognition.AndroidRecognitionManager
import com.grappim.aipal.android.recognition.RecognitionModelRetriever
import com.grappim.aipal.core.LaunchedEffectResult
import com.grappim.aipal.data.exceptions.OpenAiEmptyApiKeyException
import com.grappim.aipal.data.repo.AiPalRepo
import com.grappim.aipal.feature.chat.ChatMessageUI
import com.grappim.aipal.feature.chat.ChatState
import com.grappim.aipal.feature.chat.ListeningState
import com.grappim.aipal.feature.chat.SnackbarData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.SpeechService

class ChatViewModel(
    private val aiPalRepo: AiPalRepo,
    private val recognitionManager: AndroidRecognitionManager,
    private val recognitionModelRetriever: RecognitionModelRetriever,
) : ViewModel() {
    private val _state = MutableStateFlow(
        ChatState(
            onMessageClear = ::onMessageClear,
            onEditClientMessage = ::editResultMessage,
            toggleSTT = ::toggleSTT,
            dismissSnackbar = ::dismissSnackbar
        )
    )
    val state = _state.asStateFlow()

    private var model: Model? = null
    private var speechService: SpeechService? = null

    private val logging = logging()

    init {
        viewModelScope.launch {
            launch {
                recognitionManager.state.collect { value ->
                    val message = state.value.clientMessage + " " + value.result
                    logging.d { "here is the result: $message" }
                    _state.update { it.copy(clientMessage = message.trim()) }
                }
            }
        }
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
            result.onFailure { e ->
                _state.update {
                    it.copy(
                        snackbarMessage = LaunchedEffectResult(
                            SnackbarData(
                                message = e.message ?: "Error, try checking api key",
                                goToApiKeysScreen = e is OpenAiEmptyApiKeyException
                            )
                        )
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
            result.onFailure { e ->
                _state.update {
                    it.copy(
                        snackbarMessage = LaunchedEffectResult(
                            SnackbarData(
                                message = e.message ?: "Error, try checking api key",
                                goToApiKeysScreen = e is OpenAiEmptyApiKeyException
                            )
                        )
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
        speechService?.stop()
        speechService = null
        toggleIdleState()
    }

    private fun toggleSTT() {
        if (speechService != null) {
            turnOffSpeechService()
        } else {
            viewModelScope.launch {
                if (model != null) {
                    startListening()
                } else {
                    recognitionModelRetriever
                        .flowFromModel()
                        .catch { throwable ->
                            logging.e(throwable) {
                                "Failed to load model"
                            }
                            toggleIdleState()
                        }.collect { resultModel ->
                            logging.d { "Model loaded" }
                            model = resultModel
                            startListening()
                        }
                }
            }
        }
    }

    private fun startListening() {
        val rec = Recognizer(model, 16000.0f)
        speechService = SpeechService(rec, 16000.0f)
        speechService?.startListening(recognitionManager)

        toggleListeningState()
    }

    private fun toggleIdleState() {
        _state.update {
            it.copy(
                listeningState = ListeningState.Idle,
                fabIcon = Icons.Filled.Mic,
            )
        }
    }

    private fun toggleListeningState() {
        _state.update {
            it.copy(
                listeningState = ListeningState.Listening,
                fabIcon = Icons.Filled.MicOff,
            )
        }
    }
}

package com.grappim.aipal.android.feature.chat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.android.data.repo.AiPalRepo
import com.grappim.aipal.android.recognition.RecognitionManager
import com.grappim.aipal.android.recognition.RecognitionModelRetriever
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.SpeechService

class ChatViewModel(
    private val aiPalRepo: AiPalRepo,
    private val recognitionManager: RecognitionManager,
    private val recognitionModelRetriever: RecognitionModelRetriever,
) : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    private var model: Model? = null
    private var speechService: SpeechService? = null

    init {
        viewModelScope.launch {
            launch {
                recognitionManager.state.collect { value ->
                    val message = state.value.clientMessage + " " + value.result
                    println("here is the result: $message")
                    _state.update { it.copy(clientMessage = message) }
                }
            }
            launch {
                aiPalRepo.resultMessage.collect { value ->
                    val uiMessage = ChatMessageUI(value, false)
                    _state.update {
                        it.copy(
                            assistantMessage = value,
                            listMessages = it.listMessages + uiMessage,
                        )
                    }
                }
            }
        }
    }

    fun editResultMessage(newMsg: String) {
        viewModelScope.launch {
            _state.update { it.copy(clientMessage = newMsg) }
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            toggleSTT()
            val msgToSend = state.value.clientMessage
            val uiMessage = ChatMessageUI(msgToSend, true)
            _state.update {
                it.copy(
                    clientMessage = "",
                    listMessages = it.listMessages + uiMessage,
                )
            }

            aiPalRepo.sendMessage(msgToSend)
        }
    }

    fun toggleSTT() {
        if (speechService != null) {
            speechService?.stop()
            speechService = null
            toggleIdleState()
        } else {
            viewModelScope.launch {
                if (model != null) {
                    startListening()
                } else {
                    recognitionModelRetriever
                        .flowFromModel()
                        .catch { throwable ->
                            println(throwable)
                            toggleIdleState()
                        }.collect { resultModel ->
                            println("Model loaded")
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

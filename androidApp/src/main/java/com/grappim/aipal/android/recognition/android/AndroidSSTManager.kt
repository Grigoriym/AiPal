package com.grappim.aipal.android.recognition.android

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.ERROR_CLIENT
import androidx.annotation.MainThread
import com.grappim.aipal.core.SupportedLanguage
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.recognition.RecognitionState
import com.grappim.aipal.data.recognition.STTManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging

class AndroidSSTManager(
    private val speechRecognitionWrapper: SpeechRecognitionWrapper,
    private val localDataStorage: LocalDataStorage,
) : STTManager,
    RecognitionListener {
    private val logging = logging()

    private val _state = MutableStateFlow(RecognitionState())

    override val state: StateFlow<RecognitionState> =
        _state.asStateFlow()

    @MainThread
    override suspend fun startListening() = withContext(Dispatchers.Main) {
        setDefaultState()

        if (!speechRecognitionWrapper.isRecognitionAvailable()) {
            _state.update { it.copy(error = "Recognition is not available") }
            return@withContext
        }

        val languageCode = localDataStorage.currentLanguage.first().lang
        logging.d { "languageCode: $languageCode" }

        val intent =
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
            }
        speechRecognitionWrapper.setRecognitionListener(this@AndroidSSTManager)
        speechRecognitionWrapper.startListening(intent)
        _state.update { it.copy(isSpeaking = true) }
    }

    override suspend fun changeLanguage(supportedLanguage: SupportedLanguage) {
        speechRecognitionWrapper.cancel()
    }

    override fun stopListening() {
        setDefaultState()
        speechRecognitionWrapper.stopListening()
    }

    private fun setDefaultState() {
        _state.update { RecognitionState() }
        speechRecognitionWrapper.stopListening()
    }

    override fun cancel() {
        speechRecognitionWrapper.cancel()
    }

    override fun onReadyForSpeech(params: Bundle?) {
        _state.update { it.copy(error = "") }
    }

    override fun onEndOfSpeech() {
        _state.update { it.copy(isSpeaking = false) }
    }

    //sometimes it randomly sends an error, which does not give any useful info
    override fun onError(error: Int) {
        logging.d { "onError ErrorCode: $error" }
        if (error == ERROR_CLIENT) return
//        _state.update { it.copy(error = "ErrorCode: $error") }
    }

    override fun onResults(results: Bundle?) {
        results
            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.getOrNull(0)
            ?.let { text ->
                logging.d { "onResults: $text" }
                _state.update { it.copy(result = text) }
            }
    }

    override fun onPartialResults(partialResults: Bundle?) {
    }

    override fun onEvent(
        eventType: Int,
        params: Bundle?,
    ) {
    }

    override fun onBeginningOfSpeech() {
    }

    override fun onRmsChanged(rmsdB: Float) {
    }

    override fun onBufferReceived(buffer: ByteArray?) {
    }
}

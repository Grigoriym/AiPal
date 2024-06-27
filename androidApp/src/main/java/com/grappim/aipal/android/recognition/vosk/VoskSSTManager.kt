package com.grappim.aipal.android.recognition.vosk

import com.grappim.aipal.data.recognition.RecognitionState
import com.grappim.aipal.data.recognition.STTManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import org.lighthousegames.logging.logging
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService

class VoskSSTManager(
    private val recognitionMessageDecoder: RecognitionMessageDecoder,
    private val recognitionModelRetriever: RecognitionModelRetriever,
) : STTManager,
    RecognitionListener {
    private val logging = logging()

    private val _state = MutableStateFlow(RecognitionState())
    override val state: StateFlow<RecognitionState> = _state.asStateFlow()

    private var model: Model? = null
    private var speechService: SpeechService? = null

    override suspend fun startListening() {
        setDefaultState()
        if (model != null) {
            startRecognizer()
        } else {
            recognitionModelRetriever
                .flowFromModel()
                .catch { throwable ->
                    logging.e(throwable) {
                        "Failed to load model"
                    }
                    _state.update { it.copy(isSpeaking = false) }
                }.collect { resultModel ->
                    logging.d { "Model loaded" }
                    model = resultModel
                    startRecognizer()
                }
        }
    }

    private fun startRecognizer() {
        val rec = Recognizer(model, 16000.0f)
        speechService = SpeechService(rec, 16000.0f)
        speechService?.startListening(this)

        _state.update { it.copy(isSpeaking = true) }
    }

    private fun setDefaultState() {
        _state.update { RecognitionState() }
    }

    override fun stopListening() {
        setDefaultState()
        speechService?.stop()
        speechService = null
    }

    override fun cancel() {
        speechService?.cancel()
    }

    /**
     * It is called after silence is detected
     */
    override fun onResult(hypothesis: String?) {
        logging.d { "onResult: $hypothesis" }
        _state.update { it.copy(result = recognitionMessageDecoder.decode(hypothesis ?: "")) }
    }

    override fun onError(exception: Exception?) {
        logging.e { exception }
        _state.update { it.copy(error = exception?.message ?: "") }
    }

    /**
     * It is called when the microphone is disabled after it was enabled
     */
    override fun onFinalResult(hypothesis: String?) {
        logging.d { "onFinalResult: $hypothesis" }
    }

    override fun onTimeout() {
    }

    override fun onPartialResult(hypothesis: String?) {
    }
}

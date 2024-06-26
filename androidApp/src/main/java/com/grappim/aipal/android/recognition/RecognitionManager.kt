package com.grappim.aipal.android.recognition

import com.grappim.aipal.data.recognition.RecognitionManager
import com.grappim.aipal.data.recognition.RecognitionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.vosk.android.RecognitionListener

interface AndroidRecognitionManager : RecognitionManager, RecognitionListener

class RecognitionManagerImpl(
    private val recognitionMessageDecoder: RecognitionMessageDecoder,
) : AndroidRecognitionManager {
    private val _state = MutableStateFlow(RecognitionState())

    override val state: StateFlow<RecognitionState> = _state.asStateFlow()

    override fun onPartialResult(hypothesis: String?) {
        println("onPartialResult: $hypothesis")
    }

    /**
     * It is called after silence is detected
     */
    override fun onResult(hypothesis: String?) {
        println("onResult: $hypothesis")
        _state.update { it.copy(result = recognitionMessageDecoder.decode(hypothesis ?: "")) }
    }

    /**
     * It is called when the microphone is disabled after it was enabled
     */
    override fun onFinalResult(hypothesis: String?) {
        println("onFinalResult: $hypothesis")
    }

    override fun onError(exception: Exception?) {
        println(exception)
        _state.update { it.copy(exception = exception) }
    }

    override fun onTimeout() {
        _state.update { it.copy(isTimeout = true) }
    }
}

package com.grappim.aipal.android.recognition.vosk

import com.grappim.aipal.android.files.path.FolderPathManager
import com.grappim.aipal.android.files.vosk.ModelRetriever
import com.grappim.aipal.android.files.vosk.VoskModelAvailability
import com.grappim.aipal.android.files.vosk.VoskModelCheck
import com.grappim.aipal.android.recognition.Downloadable
import com.grappim.aipal.android.recognition.ModelAvailabilityRetrieval
import com.grappim.aipal.core.SAMPLE_RATE
import com.grappim.aipal.core.SupportedLanguage
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.recognition.ModelRetrievalResult
import com.grappim.aipal.data.recognition.ModelRetrievalState
import com.grappim.aipal.data.recognition.RecognitionState
import com.grappim.aipal.data.recognition.STTManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService

class VoskSttManager(
    private val recognitionMessageDecoder: RecognitionMessageDecoder,
    private val localDataStorage: LocalDataStorage,
    private val folderPathManager: FolderPathManager,
    private val voskModelCheck: VoskModelCheck,
    private val modelRetriever: ModelRetriever
) : STTManager,
    RecognitionListener,
    Downloadable,
    ModelAvailabilityRetrieval {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private var runningJob: Job? = null

    private val logging = logging()

    private val _state = MutableStateFlow(RecognitionState())
    override val state: StateFlow<RecognitionState> = _state.asStateFlow()

    private var model: Model? = null
    private var speechService: SpeechService? = null

    override fun initialize() {
        startModelRetriever()
    }

    private fun startModelRetriever() {
        runningJob?.cancel()
        runningJob = scope.launch {
            modelRetriever.state.collect { value ->
                logging.d { "value from VoskSttManager: $value" }
                val error =
                    if (value.modelRetrievalResult.modelRetrievalState is ModelRetrievalState.Error) {
                        (value.modelRetrievalResult.modelRetrievalState as ModelRetrievalState.Error).errorMessage
                    } else ""
                _state.update {
                    it.copy(
                        error = error,
                        modelRetrievalResult = value.modelRetrievalResult
                    )
                }
            }
        }
    }

    override fun resetToDefaultState() {
        setDefaultState()
    }

    private suspend fun getCurrentLanguage() = localDataStorage.currentLanguage.first()

    override suspend fun startListening() = withContext(Dispatchers.IO) {
        setDefaultState()
        val supportedLanguage = getCurrentLanguage()
        if (model != null) {
            startRecognizer(supportedLanguage)
        } else {
            val lang = supportedLanguage.lang
            val isModelAvailable = voskModelCheck.isModelAvailable(lang = lang)
            if (!isModelAvailable) {
                downloadModelFile(supportedLanguage)
            }
            _state.update {
                it.copy(
                    modelRetrievalResult = it.modelRetrievalResult.copy(
                        modelRetrievalState = ModelRetrievalState.ModelLoading
                    )
                )
            }
            model = getVoskModel(lang)
            startRecognizer(supportedLanguage)
        }
    }

    private fun startRecognizer(supportedLanguage: SupportedLanguage) {
        val rec = Recognizer(model, SAMPLE_RATE)
        speechService = SpeechService(rec, SAMPLE_RATE)
        requireNotNull(speechService).startListening(this)

        logging.d { "starting Recognizer with $supportedLanguage language" }

        _state.update {
            it.copy(
                isSpeaking = true,
                modelRetrievalResult = ModelRetrievalResult(supportedLanguage = supportedLanguage)
            )
        }
    }

    override suspend fun changeLanguage(supportedLanguage: SupportedLanguage) {
        logging.d { "changeLanguage: $supportedLanguage" }
        model = null

        val lang = supportedLanguage.lang
        val isModelAvailable = voskModelCheck.isModelAvailable(lang = lang)
        if (isModelAvailable) {
            model = getVoskModel(lang)
            _state.update {
                it.copy(
                    modelRetrievalResult = it.modelRetrievalResult.copy(
                        supportedLanguage = supportedLanguage,
                        modelRetrievalState = ModelRetrievalState.ModelReady
                    )
                )
            }
        }
    }

    private suspend fun getVoskModel(lang: String): Model = withContext(Dispatchers.IO) {
        Model(folderPathManager.getVoskModelFolder(lang).absolutePath)
    }

    override suspend fun isCurrentLanguageModelAvailable(): Boolean = withContext(Dispatchers.IO) {
        val supportedLanguage = getCurrentLanguage()
        val lang = supportedLanguage.lang
        voskModelCheck.isModelAvailable(
            lang = lang
        )
    }

    override suspend fun whichModelsAvailable(): List<VoskModelAvailability> =
        voskModelCheck.whichModelsAvailable()

    private fun setDefaultState() {
        scope.launch {
            val supportedLanguage = getCurrentLanguage()
            _state.update {
                RecognitionState(
                    modelRetrievalResult = ModelRetrievalResult(
                        supportedLanguage = supportedLanguage
                    )
                )
            }
        }
    }

    override fun stopListening() {
        setDefaultState()
        speechService?.stop()
        speechService = null
    }

    override fun cleanup() {
        stopListening()
        model = null

        runningJob?.cancel()
        runningJob = null
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

    override suspend fun downloadModelFile(supportedLanguage: SupportedLanguage) {
        modelRetriever.downloadModel(supportedLanguage)
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

package com.grappim.aipal.android.recognition.vosk

import com.grappim.aipal.android.files.path.FolderPathManager
import com.grappim.aipal.android.files.vosk.ModelRetriever
import com.grappim.aipal.android.files.vosk.VoskModelAvailability
import com.grappim.aipal.android.files.vosk.VoskModelCheck
import com.grappim.aipal.android.recognition.Downloadable
import com.grappim.aipal.android.recognition.ModelAvailabilityRetrieval
import com.grappim.aipal.core.SAMPLE_RATE
import com.grappim.aipal.core.SupportedLanguage
import com.grappim.aipal.core.voskModelsUrls
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.recognition.ModelRetrievalResult
import com.grappim.aipal.data.recognition.ModelRetrievalState
import com.grappim.aipal.data.recognition.RecognitionState
import com.grappim.aipal.data.recognition.STTManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
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

    private val logging = logging()

    private val _state = MutableStateFlow(RecognitionState())
    override val state: StateFlow<RecognitionState> = _state.asStateFlow()

    private var model: Model? = null
    private var speechService: SpeechService? = null

    init {
        scope.launch {
            modelRetriever.state.collect { value ->
                _state.update {
                    it.copy(
                        modelRetrievalResult = value.modelRetrievalResult
                    )
                }
            }
        }
    }

    override suspend fun startListening() = withContext(Dispatchers.IO) {
        setDefaultState()
        val supportedLanguage = localDataStorage.currentLanguage.first()
        if (model != null) {
            startRecognizer(supportedLanguage)
        } else {
            val lang = supportedLanguage.lang
            val modelFolder = folderPathManager.getVoskModelFolder(lang)
            val link = requireNotNull(voskModelsUrls[lang])
            val isModelAvailable = voskModelCheck.isModelAvailable(
                modelFolder = modelFolder,
                expectedLink = link,
                lang = lang
            )
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
            model = Model(folderPathManager.getVoskModelFolder(lang).absolutePath)
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

    override suspend fun changeLanguage(supportedLanguage: SupportedLanguage) =
        withContext(Dispatchers.IO) {
            model = null

            val lang = supportedLanguage.lang
            val modelFolder = folderPathManager.getVoskModelFolder(lang)
            val link = requireNotNull(voskModelsUrls[lang])

            val isModelAvailable = voskModelCheck.isModelAvailable(
                modelFolder = modelFolder,
                expectedLink = link,
                lang = lang
            )
            if (isModelAvailable) {
                model = Model(folderPathManager.getVoskModelFolder(lang).absolutePath)
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

    override suspend fun isCurrentLanguageModelAvailable(): Boolean = withContext(Dispatchers.IO) {
        val supportedLanguage = localDataStorage.currentLanguage.first()
        val lang = supportedLanguage.lang
        val modelFolder = folderPathManager.getVoskModelFolder(lang)
        val link = requireNotNull(voskModelsUrls[lang])

        voskModelCheck.isModelAvailable(
            modelFolder = modelFolder,
            expectedLink = link,
            lang = lang
        )
    }

    override suspend fun whichModelsAvailable(): List<VoskModelAvailability> =
        voskModelCheck.whichModelsAvailable()

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
        speechService = null

        scope.cancel()
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
//        withContext(Dispatchers.IO) {
//            val lang = supportedLanguage.lang
//            val link = requireNotNull(voskModelsUrls[lang])
//            val actualFile = File(folderPathManager.getMainFolder(), "${lang}-vosk.zip")
//            val partialFile = File.createTempFile(
//                "${lang}_voskModel",
//                "part",
//                folderPathManager.getCacheFolder()
//            )
//            fileDownloader.downloadFile(
//                link = link,
//                fileToSave = actualFile,
//                tempFile = partialFile,
//                progressCallback = { progress ->
//                    logging.d { "Download progress: $progress%" }
//                    _state.update {
//                        it.copy(
//                            modelRetrievalResult = ModelRetrievalResult(
//                                supportedLanguage = supportedLanguage,
//                                modelRetrievalState = ModelRetrievalState.Downloading(progress)
//                            )
//                        )
//                    }
//                }
//            )
//            _state.update {
//                it.copy(
//                    modelRetrievalResult = it.modelRetrievalResult.copy(
//                        modelRetrievalState = ModelRetrievalState.Downloaded()
//                    )
//                )
//            }
//            val unzipDir = folderPathManager.getVoskModelFolder(lang)
//            fileUnzipManager.unzip(
//                zipFile = actualFile,
//                destDirectory = unzipDir,
//                progressCallback = { progress ->
//                    logging.d { "Unzip progress: $progress%" }
//                    _state.update {
//                        it.copy(
//                            modelRetrievalResult = it.modelRetrievalResult.copy(
//                                modelRetrievalState = ModelRetrievalState.Unzipping(progress)
//                            )
//                        )
//                    }
//                })
//            voskModelCheck.writeDataInfo(
//                mainFolder = unzipDir,
//                downloadLink = link,
//                lang = lang
//            )
//            _state.update {
//                it.copy(
//                    modelRetrievalResult = it.modelRetrievalResult.copy(
//                        modelRetrievalState = ModelRetrievalState.Unzipped()
//                    )
//                )
//            }
//        }

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

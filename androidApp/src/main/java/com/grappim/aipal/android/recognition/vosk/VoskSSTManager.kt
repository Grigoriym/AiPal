package com.grappim.aipal.android.recognition.vosk

import com.grappim.aipal.android.files.download.FileDownloader
import com.grappim.aipal.android.files.path.FolderPathManager
import com.grappim.aipal.android.files.vosk.VoskModelCheck
import com.grappim.aipal.android.files.zip.FileUnzipManager
import com.grappim.aipal.android.recognition.Downloadable
import com.grappim.aipal.core.DEFAULT_VOSK_SAMPLE_RATE
import com.grappim.aipal.core.voskModelsUrls
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
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import java.io.File

class VoskSSTManager(
    private val recognitionMessageDecoder: RecognitionMessageDecoder,
    private val recognitionModelRetriever: RecognitionModelRetriever,
    private val localDataStorage: LocalDataStorage,
    private val folderPathManager: FolderPathManager,
    private val fileDownloader: FileDownloader,
    private val fileUnzipManager: FileUnzipManager,
    private val voskModelCheck: VoskModelCheck
) : STTManager,
    RecognitionListener,
    Downloadable {
    private val logging = logging()

    private val _state = MutableStateFlow(RecognitionState())
    override val state: StateFlow<RecognitionState> = _state.asStateFlow()

    private val _internalState = MutableStateFlow(VoskManagerState())

    private var model: Model? = null
    private var speechService: SpeechService? = null

    override suspend fun startListening() {
        setDefaultState()
        if (model != null) {
            startRecognizer()
        } else {
            try {
                val lang = localDataStorage.currentLanguage.first().lang
                val modelFolder = folderPathManager.getVoskModelFolder(lang)
                val link = requireNotNull(voskModelsUrls[lang])
                val isModelAvailable = voskModelCheck.isModelAvailable(
                    modelFolder = modelFolder,
                    expectedLink = link,
                    lang = lang
                )
                if (!isModelAvailable) {
                    downloadFile()
                }
                model = Model(folderPathManager.getVoskModelFolder(lang).absolutePath)

            } catch (e: Exception) {
                logging.e { e }
            }
            startRecognizer()
        }
    }

    private fun startRecognizer() {
        val rec = Recognizer(model, DEFAULT_VOSK_SAMPLE_RATE)
        speechService = SpeechService(rec, DEFAULT_VOSK_SAMPLE_RATE)
        requireNotNull(speechService).startListening(this)

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
        speechService = null
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

    override suspend fun downloadFile() = withContext(Dispatchers.IO) {
        val lang = localDataStorage.currentLanguage.first().lang
        val link = requireNotNull(voskModelsUrls[lang])
        val actualFile = File(folderPathManager.getMainFolder(), "${lang}-vosk.zip")
        val partialFile = File.createTempFile(
            "${lang}_voskModel",
            "part",
            folderPathManager.getCacheFolder()
        )
        fileDownloader.downloadFile(
            link = link,
            fileToSave = actualFile,
            tempFile = partialFile,
            progressCallback = { currentBytes, totalBytes ->
                val progress = if (totalBytes > 0) (currentBytes * 100 / totalBytes) else 0
                logging.d { "Download progress: $progress%" }
            }
        )
        val unzipDir = folderPathManager.getVoskModelFolder(lang)
        fileUnzipManager.unzip(actualFile, unzipDir)
        voskModelCheck.writeDataInfo(
            mainFolder = unzipDir,
            downloadLink = link,
            lang = lang
        )
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

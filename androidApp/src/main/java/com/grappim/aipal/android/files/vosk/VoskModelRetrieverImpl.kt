package com.grappim.aipal.android.files.vosk

import com.grappim.aipal.android.files.download.FileDownloader
import com.grappim.aipal.android.files.path.FolderPathManager
import com.grappim.aipal.android.files.zip.FileUnzipManager
import com.grappim.aipal.core.SupportedLanguage
import com.grappim.aipal.core.voskModelsUrls
import com.grappim.aipal.data.recognition.ModelRetrievalResult
import com.grappim.aipal.data.recognition.ModelRetrievalState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging
import java.io.File

class VoskModelRetrieverImpl(
    private val folderPathManager: FolderPathManager,
    private val fileDownloader: FileDownloader,
    private val fileUnzipManager: FileUnzipManager,
    private val voskModelCheck: VoskModelCheck
) : ModelRetriever {

    private val _state = MutableSharedFlow<ModelRetrieverState>(replay = 1)
    override val state: SharedFlow<ModelRetrieverState> = _state.asSharedFlow()

    private val logging = logging()

    init {
        _state.tryEmit(ModelRetrieverState())
    }

    private data class PreparedData(
        val lang: String,
        val link: String,
        val zipFileToSave: File,
        val tempFileToSave: File
    )

    private suspend fun prepareData(supportedLanguage: SupportedLanguage): PreparedData =
        withContext(Dispatchers.IO) {
            val lang = supportedLanguage.lang
            val link = requireNotNull(voskModelsUrls[lang])
            val zipFileToSave = File(folderPathManager.getMainFolder(), "${lang}-vosk.zip")
            val tempFileToSave = File.createTempFile(
                "${lang}_voskModel",
                "part",
                folderPathManager.getCacheFolder()
            )
            PreparedData(
                lang = lang,
                link = link,
                zipFileToSave = zipFileToSave,
                tempFileToSave = tempFileToSave
            )
        }

    override suspend fun downloadModel(supportedLanguage: SupportedLanguage) {
        val (lang, link, zipFileToSave, tempFileToSave) = prepareData(supportedLanguage)
        try {
            fileDownloader.downloadFile(
                link = link,
                fileToSave = zipFileToSave,
                tempFile = tempFileToSave,
                progressCallback = { progress ->
                    logging.d { "Download progress: $progress%" }
                    _state.emit(
                        ModelRetrieverState(
                            modelRetrievalResult = ModelRetrievalResult(
                                supportedLanguage = supportedLanguage,
                                modelRetrievalState = ModelRetrievalState.Downloading(progress)
                            )
                        )
                    )
                }
            )
            _state.emit(
                ModelRetrieverState(
                    modelRetrievalResult = ModelRetrievalResult(
                        supportedLanguage = supportedLanguage,
                        modelRetrievalState = ModelRetrievalState.Downloaded
                    )
                )
            )
            val unzipDir = folderPathManager.getVoskModelFolder(lang)
            fileUnzipManager.unzip(
                zipFile = zipFileToSave,
                destDirectory = unzipDir,
                progressCallback = { progress ->
                    logging.d { "Unzip progress: $progress%" }
                    _state.emit(
                        ModelRetrieverState(
                            modelRetrievalResult = ModelRetrievalResult(
                                supportedLanguage = supportedLanguage,
                                modelRetrievalState = ModelRetrievalState.Unzipping(progress)
                            )
                        )
                    )
                })
            voskModelCheck.writeDataInfo(
                mainFolder = unzipDir,
                downloadLink = link,
                lang = lang
            )
            _state.emit(
                ModelRetrieverState(
                    modelRetrievalResult = ModelRetrievalResult(
                        supportedLanguage = supportedLanguage,
                        modelRetrievalState = ModelRetrievalState.Unzipped
                    )
                )
            )

            _state.emit(
                ModelRetrieverState(
                    modelRetrievalResult = ModelRetrievalResult(
                        supportedLanguage = supportedLanguage,
                        modelRetrievalState = ModelRetrievalState.Initial
                    )
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logging.e(e) { "Error downloading or unzipping model for language $lang" }
            _state.emit(
                ModelRetrieverState(
                    modelRetrievalResult = ModelRetrievalResult(
                        supportedLanguage = supportedLanguage,
                        modelRetrievalState = ModelRetrievalState.Error(
                            e.message ?: "Unknown error"
                        )
                    )
                )
            )
            clean(zipFileToSave, tempFileToSave, lang)
        }
    }

    private suspend fun clean(zipFileToSave: File, tempFileToSave: File, lang: String) =
        withContext(Dispatchers.IO) {
            zipFileToSave.delete()
            tempFileToSave.delete()
            folderPathManager.getVoskModelFolder(lang).deleteRecursively()
        }
}

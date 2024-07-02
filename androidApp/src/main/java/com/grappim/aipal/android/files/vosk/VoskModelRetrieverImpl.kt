package com.grappim.aipal.android.files.vosk

import com.grappim.aipal.android.files.download.FileDownloader
import com.grappim.aipal.android.files.path.FolderPathManager
import com.grappim.aipal.android.files.zip.FileUnzipManager
import com.grappim.aipal.core.SupportedLanguage
import com.grappim.aipal.core.voskModelsUrls
import com.grappim.aipal.data.recognition.ModelRetrievalResult
import com.grappim.aipal.data.recognition.ModelRetrievalState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging
import java.io.File

class VoskModelRetrieverImpl(
    private val folderPathManager: FolderPathManager,
    private val fileDownloader: FileDownloader,
    private val fileUnzipManager: FileUnzipManager,
    private val voskModelCheck: VoskModelCheck
) : ModelRetriever {

    private val _state = MutableStateFlow(ModelRetrieverState())
    override val state: StateFlow<ModelRetrieverState> = _state.asStateFlow()

    private val logging = logging()

    override suspend fun downloadModel(supportedLanguage: SupportedLanguage) =
        withContext(Dispatchers.IO) {
            val lang = supportedLanguage.lang
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
                progressCallback = { progress ->
                    logging.d { "Download progress: $progress%" }
                    _state.update {
                        it.copy(
                            modelRetrievalResult = ModelRetrievalResult(
                                supportedLanguage = supportedLanguage,
                                modelRetrievalState = ModelRetrievalState.Downloading(progress)
                            )
                        )
                    }
                }
            )
            _state.update {
                it.copy(
                    modelRetrievalResult = it.modelRetrievalResult.copy(
                        supportedLanguage = supportedLanguage,
                        modelRetrievalState = ModelRetrievalState.Downloaded()
                    )
                )
            }
            val unzipDir = folderPathManager.getVoskModelFolder(lang)
            fileUnzipManager.unzip(
                zipFile = actualFile,
                destDirectory = unzipDir,
                progressCallback = { progress ->
                    logging.d { "Unzip progress: $progress%" }
                    _state.update {
                        it.copy(
                            modelRetrievalResult = it.modelRetrievalResult.copy(
                                supportedLanguage = supportedLanguage,
                                modelRetrievalState = ModelRetrievalState.Unzipping(progress)
                            )
                        )
                    }
                })
            voskModelCheck.writeDataInfo(
                mainFolder = unzipDir,
                downloadLink = link,
                lang = lang
            )
            _state.update {
                it.copy(
                    modelRetrievalResult = it.modelRetrievalResult.copy(
                        supportedLanguage = supportedLanguage,
                        modelRetrievalState = ModelRetrievalState.Unzipped()
                    )
                )
            }
        }
}

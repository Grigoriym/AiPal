package com.grappim.aipal.android.files.vosk

import com.grappim.aipal.android.files.path.FolderPathManager
import com.grappim.aipal.core.SupportedLanguage
import com.grappim.aipal.core.voskModelsUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class VoskModelCheckImpl(
    private val json: Json,
    private val folderPathManager: FolderPathManager
) : VoskModelCheck {

    override suspend fun isModelAvailable(
        modelFolder: File,
        expectedLink: String,
        lang: String
    ): Boolean =
        withContext(Dispatchers.IO) {
            if (!modelFolder.exists() ||
                !modelFolder.isDirectory ||
                modelFolder.listFiles()?.isEmpty() == true
            ) {
                return@withContext false
            }
            val dataInfo = readDataInfo(modelFolder, lang)
            dataInfo?.downloadLink == expectedLink
        }

    override suspend fun whichModelsAvailable(): List<VoskModelAvailability> =
        withContext(Dispatchers.IO) {
            SupportedLanguage.entries.map { entry ->
                val modelFolder = folderPathManager.getVoskModelFolder(entry.lang)
                val link = requireNotNull(voskModelsUrls[entry.lang])
                val isAvailable = isModelAvailable(
                    modelFolder = modelFolder,
                    expectedLink = link,
                    lang = entry.lang
                )

                VoskModelAvailability(
                    supportedLanguage = entry,
                    isAvailable = isAvailable
                )
            }
        }

    override suspend fun writeDataInfo(mainFolder: File, downloadLink: String, lang: String) =
        withContext(Dispatchers.IO) {
            val dataInfo = VoskModelDataInfo(downloadLink)
            val jsonData = json.encodeToString(dataInfo)
            getJsonFile(mainFolder, lang).writeText(jsonData)
        }

    private fun readDataInfo(mainFolder: File, lang: String): VoskModelDataInfo? {
        val file = getJsonFile(mainFolder, lang)
        return if (file.exists()) {
            val jsonFile = file.readText()
            json.decodeFromString<VoskModelDataInfo>(jsonFile)
        } else null
    }

    private fun getJsonFile(mainFolder: File, lang: String): File =
        File(mainFolder, "${lang}_data_info.json")
}
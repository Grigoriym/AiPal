package com.grappim.aipal.android.files.vosk

import java.io.File

interface VoskModelCheck {
    suspend fun isModelAvailable(lang: String): Boolean
    suspend fun writeDataInfo(mainFolder: File, downloadLink: String, lang: String)
    suspend fun whichModelsAvailable(): List<VoskModelAvailability>
}

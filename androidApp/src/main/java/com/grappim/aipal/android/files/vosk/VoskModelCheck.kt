package com.grappim.aipal.android.files.vosk

import java.io.File

interface VoskModelCheck {
    suspend fun isModelAvailable(modelFolder: File, expectedLink: String, lang: String): Boolean
    suspend fun writeDataInfo(mainFolder: File, downloadLink: String, lang: String)
}
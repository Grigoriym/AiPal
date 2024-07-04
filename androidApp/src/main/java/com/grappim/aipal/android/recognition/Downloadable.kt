package com.grappim.aipal.android.recognition

import com.grappim.aipal.core.SupportedLanguage

interface Downloadable {
    suspend fun downloadModelFile(supportedLanguage: SupportedLanguage)
}

package com.grappim.aipal.android.files.vosk

import com.grappim.aipal.core.SupportedLanguage
import kotlinx.coroutines.flow.SharedFlow

interface ModelRetriever {
    val state: SharedFlow<ModelRetrieverState>

    suspend fun downloadModel(supportedLanguage: SupportedLanguage)
}

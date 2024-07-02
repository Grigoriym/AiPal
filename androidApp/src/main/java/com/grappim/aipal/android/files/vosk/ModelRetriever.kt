package com.grappim.aipal.android.files.vosk

import com.grappim.aipal.core.SupportedLanguage
import kotlinx.coroutines.flow.StateFlow

interface ModelRetriever {
    val state: StateFlow<ModelRetrieverState>

    suspend fun downloadModel(supportedLanguage: SupportedLanguage)
}

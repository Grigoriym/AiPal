package com.grappim.aipal.data.recognition

import com.grappim.aipal.core.SupportedLanguage

data class RecognitionState(
    val result: String = "",
    val error: String = "",
    val isSpeaking: Boolean = false,
    val modelRetrievalResult: ModelRetrievalResult = ModelRetrievalResult()
)

data class ModelRetrievalResult(
    val supportedLanguage: SupportedLanguage = SupportedLanguage.getDefault(),
    val modelRetrievalState: ModelRetrievalState = ModelRetrievalState.Initial
)

sealed interface ModelRetrievalState {
    data object Initial : ModelRetrievalState

    data class Downloading(val progress: Int = 0) : ModelRetrievalState
    data class Downloaded(val error: Throwable? = null) : ModelRetrievalState

    data class Unzipping(val progress: Int = 0) : ModelRetrievalState
    data class Unzipped(val error: Throwable? = null) : ModelRetrievalState

    data object ModelLoading : ModelRetrievalState
    data object ModelReady : ModelRetrievalState
}

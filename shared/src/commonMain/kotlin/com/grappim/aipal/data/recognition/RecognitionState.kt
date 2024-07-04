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
) {
    fun showAlertDialog(): Boolean =
        modelRetrievalState !is ModelRetrievalState.ModelReady &&
                modelRetrievalState !is ModelRetrievalState.Initial &&
                modelRetrievalState !is ModelRetrievalState.ModelLoading &&
                modelRetrievalState !is ModelRetrievalState.Error

    fun isDownloading(): Boolean =
        modelRetrievalState is ModelRetrievalState.Downloading ||
                modelRetrievalState is ModelRetrievalState.Unzipping ||
                modelRetrievalState is ModelRetrievalState.Downloaded ||
                modelRetrievalState is ModelRetrievalState.Unzipped
}

sealed interface ModelRetrievalState {
    data object Initial : ModelRetrievalState

    data class Downloading(val progress: Int = 0) : ModelRetrievalState
    data object Downloaded : ModelRetrievalState

    data class Unzipping(val progress: Int = 0) : ModelRetrievalState
    data object Unzipped : ModelRetrievalState

    data object ModelLoading : ModelRetrievalState
    data object ModelReady : ModelRetrievalState

    data class Error(val errorMessage: String) : ModelRetrievalState
}

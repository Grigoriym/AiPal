package com.grappim.aipal.android.feature.stt

import com.grappim.aipal.android.files.vosk.VoskModelAvailability
import com.grappim.aipal.data.recognition.ModelRetrievalState

data class VoskModelUI(
    val voskModelAvailability: VoskModelAvailability,
    val voskModelUIState: ModelRetrievalState = ModelRetrievalState.Initial
)

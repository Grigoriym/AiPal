package com.grappim.aipal.data.recognition

import kotlinx.coroutines.flow.StateFlow

interface RecognitionManager {
    val state: StateFlow<RecognitionState>
}

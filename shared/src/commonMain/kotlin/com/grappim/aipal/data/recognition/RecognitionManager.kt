package com.grappim.aipal.data.recognition

import androidx.annotation.MainThread
import kotlinx.coroutines.flow.StateFlow

interface STTManager {
    val state: StateFlow<RecognitionState>

    @MainThread
    suspend fun startListening()

    fun stopListening()

    fun cancel()
}

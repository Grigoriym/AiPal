package com.grappim.aipal.data.recognition

import androidx.annotation.MainThread
import com.grappim.aipal.core.SupportedLanguage
import kotlinx.coroutines.flow.StateFlow

interface STTManager {
    val state: StateFlow<RecognitionState>

    @MainThread
    suspend fun startListening()

    suspend fun changeLanguage(supportedLanguage: SupportedLanguage)

    fun stopListening()

    fun cancel()
}

package com.grappim.aipal.data.recognition

import com.grappim.aipal.core.SupportedLanguage
import kotlinx.coroutines.flow.StateFlow

interface STTManager {
    val state: StateFlow<RecognitionState>

    suspend fun startListening()

    suspend fun changeLanguage(supportedLanguage: SupportedLanguage)

    fun stopListening()

    fun cleanup()

    fun initialize()

    fun resetToDefaultState()
}

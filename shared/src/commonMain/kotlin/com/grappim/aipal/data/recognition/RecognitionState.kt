package com.grappim.aipal.data.recognition

data class RecognitionState(
    val result: String = "",
    val error: String = "",
    val isSpeaking: Boolean = false,
)

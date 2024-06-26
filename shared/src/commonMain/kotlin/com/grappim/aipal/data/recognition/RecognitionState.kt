package com.grappim.aipal.data.recognition

data class RecognitionState(
    val exception: Exception? = null,
    val isTimeout: Boolean = false,
    val result: String = "",
)

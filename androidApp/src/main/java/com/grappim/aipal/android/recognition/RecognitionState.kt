package com.grappim.aipal.android.recognition

import java.lang.Exception

data class RecognitionState(
    val exception: Exception? = null,
    val isTimeout: Boolean = false,
    val result: String = "",
)

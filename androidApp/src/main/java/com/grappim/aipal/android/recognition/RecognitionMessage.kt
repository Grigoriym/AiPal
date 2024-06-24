package com.grappim.aipal.android.recognition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecognitionMessage(
    @SerialName("text")
    val text: String,
)

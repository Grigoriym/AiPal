package com.grappim.aipal.android.files.vosk

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoskModelDataInfo(@SerialName("downloadLink") val downloadLink: String)

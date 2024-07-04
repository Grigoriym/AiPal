package com.grappim.aipal.android.files.vosk

import com.grappim.aipal.core.SupportedLanguage

data class VoskModelAvailability(
    val supportedLanguage: SupportedLanguage,
    val isAvailable: Boolean
)

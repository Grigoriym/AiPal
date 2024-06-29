package com.grappim.aipal.android.feature.stt

import com.grappim.aipal.core.SupportedLanguage
import com.grappim.aipal.data.recognition.CurrentSTTManager

data class SttSettingsState(
    val currentSTTManager: CurrentSTTManager,
    val onSstManagerChange: (CurrentSTTManager) -> Unit,
    val description: String = "",
    val currentLanguage: SupportedLanguage,
    val onSetCurrentLanguage: (String) -> Unit,
    val languages: Set<String>,
)

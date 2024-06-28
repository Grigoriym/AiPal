package com.grappim.aipal.android.feature.language

import com.grappim.aipal.core.SupportedLanguage
import com.grappim.aipal.data.recognition.CurrentSSTManager

data class SstSettingsState(
    val currentSstManager: CurrentSSTManager,
    val onSstManagerChange: (CurrentSSTManager) -> Unit,
    val description: String = "",
    val currentLanguage: SupportedLanguage,
    val onSetCurrentLanguage: (String) -> Unit,
    val languages: Set<String>,
)

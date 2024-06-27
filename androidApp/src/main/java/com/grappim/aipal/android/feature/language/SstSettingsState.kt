package com.grappim.aipal.android.feature.language

import com.grappim.aipal.data.recognition.CurrentSSTManager

data class SstSettingsState(
    val currentSstManager: CurrentSSTManager,
    val onSstManagerChange: (CurrentSSTManager) -> Unit,
    val description: String = "",
)

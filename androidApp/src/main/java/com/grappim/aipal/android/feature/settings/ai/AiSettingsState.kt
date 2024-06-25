package com.grappim.aipal.android.feature.settings.ai

import com.grappim.aipal.android.core.DEFAULT_MODEL
import com.grappim.aipal.android.core.DEFAULT_TEMPERATURE

data class AiSettingsState(
    val models: Set<String> = emptySet(),
    val selectedModel: String = DEFAULT_MODEL,
    val tempValue: Double = DEFAULT_TEMPERATURE,
    val onSetTempValue: (Double) -> Unit,
    val onGetModels: () -> Unit,
    val onSetModel: (String) -> Unit,
    val applySettings: () -> Unit,
)

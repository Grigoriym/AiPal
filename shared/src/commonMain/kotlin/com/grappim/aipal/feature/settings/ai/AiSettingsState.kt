package com.grappim.aipal.feature.settings.ai

import com.grappim.aipal.core.DEFAULT_MODEL
import com.grappim.aipal.core.DEFAULT_TEMPERATURE
import com.grappim.aipal.core.LaunchedEffectResult

data class AiSettingsState(
    val models: Set<String> = emptySet(),
    val selectedModel: String = DEFAULT_MODEL,
    val tempValue: Double = DEFAULT_TEMPERATURE,
    val onSetTempValue: (Double) -> Unit,
    val onGetModels: () -> Unit,
    val onSetModel: (String) -> Unit,
    val applyTemp: () -> Unit,

    val snackbarMessage: LaunchedEffectResult<String> = LaunchedEffectResult(""),
)

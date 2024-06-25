package com.grappim.aipal.android.feature.settings

import com.grappim.aipal.android.core.DEFAULT_BEHAVIOR
import com.grappim.aipal.android.core.DEFAULT_MODEL
import com.grappim.aipal.android.core.DEFAULT_TEMPERATURE
import com.grappim.aipal.android.data.local.DarkThemeConfig

data class SettingsState(
    val models: Set<String> = emptySet(),
    val selectedModel: String = DEFAULT_MODEL,
    val behavior: String = DEFAULT_BEHAVIOR,
    val showAiSettings: Boolean = false,
    val tempValue: Double = DEFAULT_TEMPERATURE,
    val onSetTempValue: (Double) -> Unit,
    val applySettings: () -> Unit,
    val onGetModels: () -> Unit,
    val onSetModel: (String) -> Unit,
    val onBehaviorValueChange: (String) -> Unit,
    val onSetBehavior: () -> Unit,
    val onDarkThemeConfigClicked: (DarkThemeConfig) -> Unit,
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.default(),
    val isUiSettingsVisible: Boolean = false,
    val onShowUiSettings: (Boolean) -> Unit
)

package com.grappim.aipal.feature.settings

import com.grappim.aipal.core.DEFAULT_BEHAVIOR
import com.grappim.aipal.data.model.DarkThemeConfig

data class SettingsState(
    val behavior: String = DEFAULT_BEHAVIOR,
    val onBehaviorValueChange: (String) -> Unit,
    val onSetBehavior: () -> Unit,
    val onDarkThemeConfigClicked: (DarkThemeConfig) -> Unit,
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.default(),
    val isUiSettingsVisible: Boolean = false,
    val onShowUiSettings: (Boolean) -> Unit
)

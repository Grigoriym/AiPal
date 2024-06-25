package com.grappim.aipal.android.feature.settings

import com.grappim.aipal.android.core.DEFAULT_BEHAVIOR
import com.grappim.aipal.android.core.DEFAULT_MODEL
import com.grappim.aipal.android.core.DEFAULT_TEMPERATURE
import com.grappim.aipal.android.data.local.DarkThemeConfig

data class SettingsState(
    val behavior: String = DEFAULT_BEHAVIOR,
    val onBehaviorValueChange: (String) -> Unit,
    val onSetBehavior: () -> Unit,
    val onDarkThemeConfigClicked: (DarkThemeConfig) -> Unit,
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.default(),
    val isUiSettingsVisible: Boolean = false,
    val onShowUiSettings: (Boolean) -> Unit
)

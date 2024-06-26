package com.grappim.aipal.feature.main

import com.grappim.aipal.data.model.DarkThemeConfig

data class MainState(
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM
)

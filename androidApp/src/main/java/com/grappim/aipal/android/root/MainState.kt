package com.grappim.aipal.android.root

import com.grappim.aipal.android.data.local.DarkThemeConfig

data class MainState(
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM
)

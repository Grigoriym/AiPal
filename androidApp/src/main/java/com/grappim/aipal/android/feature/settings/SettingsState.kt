package com.grappim.aipal.android.feature.settings

import com.grappim.aipal.android.data.repo.DEFAULT_BEHAVIOR
import com.grappim.aipal.android.data.repo.DEFAULT_MODEL

data class SettingsState(
    val models: List<String> = emptyList(),
    val selectedModel: String = DEFAULT_MODEL,
    val behavior: String = DEFAULT_BEHAVIOR,
)

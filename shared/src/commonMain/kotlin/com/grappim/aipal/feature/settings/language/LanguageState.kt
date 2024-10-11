package com.grappim.aipal.feature.settings.language

import com.grappim.aipal.core.SupportedLanguage

data class LanguageState(
    val currentLanguage: SupportedLanguage,
    val languages: Set<String>,
    val onSetCurrentLanguage: (String) -> Unit,
)

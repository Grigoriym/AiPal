package com.grappim.aipal.android.feature.prompts

data class PromptsState(
    val translationPrompt: String = "",
    val onSetTranslationPrompt: (String) -> Unit,
    val behavior: String = "",
    val onSetBehavior: (String) -> Unit,
    val saveBehavior: () -> Unit,
    val saveTranslationPrompt: () -> Unit
)

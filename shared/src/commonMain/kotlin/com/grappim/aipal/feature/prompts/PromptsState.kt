package com.grappim.aipal.feature.prompts

data class PromptsState(
    val translationPrompt: String = "",
    val onSetTranslationPrompt: (String) -> Unit,
    val saveTranslationPrompt: () -> Unit,

    val behavior: String = "",
    val onSetBehavior: (String) -> Unit,
    val saveBehavior: () -> Unit,

    val spellingCheckPrompt: String = "",
    val onSetSpelling: (String) -> Unit,
    val saveSpelling: () -> Unit,

    val aiAnswerFixPrompt: String = "",
    val onSetAiAnswerFixPrompt: (String) -> Unit,
    val saveAiAnswerFixPrompt: () -> Unit,
)

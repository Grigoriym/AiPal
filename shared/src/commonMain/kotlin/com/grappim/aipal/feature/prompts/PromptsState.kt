package com.grappim.aipal.feature.prompts

data class PromptsState(
    val promptElements: List<PromptStateElement> = emptyList(),

    val onUpdatePromptElement: (index: Int, newValue: String) -> Unit
)

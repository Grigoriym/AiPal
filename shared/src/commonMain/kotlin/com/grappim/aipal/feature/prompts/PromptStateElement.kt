package com.grappim.aipal.feature.prompts

import com.grappim.aipal.data.db.model.PromptType

data class PromptStateElement(
    val title: String,
    val value: String,
    val label: String,
    val onSave: (index: Int) -> Unit,
    val saveButtonText: String,
    val languageId: Long,
    val type: PromptType
)

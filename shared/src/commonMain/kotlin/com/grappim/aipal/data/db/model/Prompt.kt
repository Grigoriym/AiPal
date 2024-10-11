package com.grappim.aipal.data.db.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Prompt(
    @SerialName("id")
    val id: Long,
    @SerialName("content")
    val content: String,
    @SerialName("languageId")
    val languageId: Long,
    @SerialName("promptType")
    val promptType: PromptType
)

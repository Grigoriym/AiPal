package com.grappim.aipal.data.model

import com.aallam.openai.api.core.Role

data class Message(
    val text: String,
    val role: Role,
)

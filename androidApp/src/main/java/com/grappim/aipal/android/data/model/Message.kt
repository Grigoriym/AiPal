package com.grappim.aipal.android.data.model

import com.aallam.openai.api.core.Role

data class Message(
    val text: String,
    val role: Role,
)

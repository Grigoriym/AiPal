package com.grappim.aipal.data.model

import com.aallam.openai.api.core.Role

data class Message(
    val id: String,
    val text: String,
    val role: Role,
    val messageType: MessageType = MessageType.OTHER
)

enum class MessageType {
    BEHAVIOR,
    LANGUAGE,
    AI_FIX,
    OTHER
}

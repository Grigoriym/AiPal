package com.grappim.aipal.feature.chat

data class ChatMessageUI(
    val uuid: String,
    val message: String,
    val isUserMessage: Boolean,
    val translation: String = "",
    val spellingCheck: String = "",
    val isMessageDelivered: Boolean = true
)

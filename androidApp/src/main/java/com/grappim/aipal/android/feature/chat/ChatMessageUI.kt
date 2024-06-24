package com.grappim.aipal.android.feature.chat

import java.util.UUID

data class ChatMessageUI(
    val uuid: String = UUID.randomUUID().toString(),
    val message: String,
    val isUserMessage: Boolean,
    val translation: String = "",
)

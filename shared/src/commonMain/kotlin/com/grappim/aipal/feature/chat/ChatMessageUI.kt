package com.grappim.aipal.feature.chat

import com.benasher44.uuid.uuid4

data class ChatMessageUI(
    val uuid: String = uuid4().toString(),
    val message: String,
    val isUserMessage: Boolean,
    val translation: String = "",
    val spellingCheck: String = ""
)

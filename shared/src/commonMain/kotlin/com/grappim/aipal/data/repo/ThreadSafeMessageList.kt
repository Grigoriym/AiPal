package com.grappim.aipal.data.repo

import com.grappim.aipal.data.model.Message
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ThreadSafeMessageList {
    private val mutex = Mutex()
    private var messages = listOf<Message>()

    suspend fun addMessage(message: Message) {
        mutex.withLock {
            messages = messages + message
        }
    }

    suspend fun removeMessageById(id: String) {
        mutex.withLock {
            messages = messages.filterNot { it.id == id }
        }
    }

    suspend fun getMessages(): List<Message> = mutex.withLock { messages }
}
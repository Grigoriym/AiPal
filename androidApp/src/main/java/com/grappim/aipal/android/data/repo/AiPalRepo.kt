package com.grappim.aipal.android.data.repo

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.grappim.aipal.android.core.DEFAULT_BEHAVIOR
import com.grappim.aipal.android.data.local.LocalDataStorage
import com.grappim.aipal.android.data.model.Message
import com.grappim.aipal.android.data.service.OpenAiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

interface AiPalRepo {
    val resultMessage: StateFlow<String>

    suspend fun getModels(): List<Model>

    suspend fun sendMessage(msg: String)

    fun setBehavior(msg: String)

    suspend fun translateMessage(msg: String): String
}

class AiPalRepoImpl(
    private val openAiClient: OpenAiClient,
    private val localDataStorage: LocalDataStorage,
) : AiPalRepo {
    override val resultMessage = MutableStateFlow("")

    private val messages = mutableListOf<Message>()

    init {
        setBehavior(DEFAULT_BEHAVIOR)
    }

    override suspend fun translateMessage(msg: String): String =
        withContext(Dispatchers.IO) {
            runCatching {
                val chatCompletionRequest =
                    ChatCompletionRequest(
                        temperature = localDataStorage.tempFlow.first(),
                        model = ModelId(localDataStorage.currentGptModel.first()),
                        messages =
                            listOf(
                                ChatMessage(
                                    role = Role.System,
                                    content = "Translate the next message to English, Give me only the translation and nothing else",
                                ),
                                ChatMessage(
                                    role = Role.User,
                                    content = "\"$msg\"",
                                ),
                            ),
                    )
                val completion = openAiClient.openAi.chatCompletion(chatCompletionRequest)
                val receivedMessage =
                    completion.choices
                        .first()
                        .message
                receivedMessage.content ?: ""
            }.onFailure {
                println(it)
            }.getOrDefault("")
        }

    override fun setBehavior(msg: String) {
        val presentBehavior = messages.find { it.role == Role.System }
        if (presentBehavior == null) {
            messages.add(Message(msg, Role.System))
        } else {
            val newBehavior = presentBehavior.copy(text = msg)
            messages[messages.indexOf(presentBehavior)] = newBehavior
        }
    }

    override suspend fun getModels(): List<Model> =
        withContext(Dispatchers.IO) {
            openAiClient.openAi.models()
        }

    override suspend fun sendMessage(msg: String): Unit =
        withContext(Dispatchers.IO) {
            messages.add(Message(msg, Role.User))
            runCatching {
                val chatCompletionRequest =
                    ChatCompletionRequest(
                        temperature = localDataStorage.tempFlow.first(),
                        model = ModelId(localDataStorage.currentGptModel.first()),
                        messages =
                            messages.map {
                                ChatMessage(
                                    role = it.role,
                                    content = it.text,
                                )
                            },
                    )
                val completion = openAiClient.openAi.chatCompletion(chatCompletionRequest)
                val receivedMessage =
                    completion.choices
                        .first()
                        .message
                val result = receivedMessage.content ?: ""
                messages.add(Message(result, Role.Assistant))
                println(result)
                resultMessage.update { result }
            }.onFailure {
                println(it)
            }
        }
}

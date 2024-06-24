package com.grappim.aipal.android.data.repo

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.grappim.aipal.android.data.model.Message
import com.grappim.aipal.android.data.model.ModelToUse
import com.grappim.aipal.android.data.service.OpenAiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

const val DEFAULT_MODEL = "gpt-3.5-turbo"
const val DEFAULT_BEHAVIOR =
    "You are my German friend with whom I want to practice German. " +
        "When I finish this conversation, " +
        "show me my errors and provide ways to fix them."

interface AiPalRepo {
    val resultMessage: StateFlow<String>

    suspend fun getModels(): List<Model>

    suspend fun sendMessage(msg: String)

    fun setBehavior(msg: String)

    fun setModel(model: String)
}

class AiPalRepoImpl(
    private val openAiClient: OpenAiClient,
) : AiPalRepo {
    override val resultMessage = MutableStateFlow("")

    private val messages = mutableListOf<Message>()

    private val mutex = Mutex()
    private var _currentModelToUse = ModelToUse(DEFAULT_MODEL)
    private var currentModelToUse: ModelToUse
        get() = runBlocking { mutex.withLock { _currentModelToUse } }
        set(newValue) = runBlocking { mutex.withLock { _currentModelToUse = newValue } }

    init {
        setBehavior(DEFAULT_BEHAVIOR)
    }

    override fun setModel(model: String) {
        currentModelToUse = currentModelToUse.copy(modelId = model)
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
                        temperature = 0.3,
                        model = ModelId(currentModelToUse.modelId),
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

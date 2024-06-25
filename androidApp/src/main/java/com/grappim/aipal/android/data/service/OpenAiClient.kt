package com.grappim.aipal.android.data.service

import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.grappim.aipal.android.BuildConfig

interface OpenAiClient {
    val openAi: OpenAI
}

class OpenAiClientImpl : OpenAiClient {
    override val openAi: OpenAI =
        OpenAI(
            token = BuildConfig.openAiApiKey,
            logging =
                LoggingConfig(
                    logLevel = LogLevel.All,
                ),
        )
}

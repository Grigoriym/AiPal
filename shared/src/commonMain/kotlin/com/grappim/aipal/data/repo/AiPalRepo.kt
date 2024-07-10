package com.grappim.aipal.data.repo

import com.grappim.aipal.data.model.ResultMessage

interface AiPalRepo {
    suspend fun getModels(): Result<List<String>>

    suspend fun sendMessage(msg: String, msgId: String): Result<ResultMessage>

    suspend fun translateMessage(msg: String): Result<String>

    suspend fun checkSpelling(msg: String): Result<String>
}

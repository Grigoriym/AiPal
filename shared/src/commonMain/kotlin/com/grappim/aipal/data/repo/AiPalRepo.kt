package com.grappim.aipal.data.repo

interface AiPalRepo {
    suspend fun getModels(): Result<List<String>>

    suspend fun sendMessage(msg: String): Result<String>

    fun setBehavior(msg: String)

    suspend fun translateMessage(msg: String): Result<String>
}

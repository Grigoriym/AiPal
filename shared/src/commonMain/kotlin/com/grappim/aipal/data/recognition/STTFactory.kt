package com.grappim.aipal.data.recognition

interface STTFactory {
    fun getSSTManager(currentSTTManager: CurrentSTTManager): STTManager
}

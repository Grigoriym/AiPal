package com.grappim.aipal.android.recognition

import com.grappim.aipal.android.files.vosk.VoskModelAvailability

interface ModelAvailabilityRetrieval {
    suspend fun whichModelsAvailable(): List<VoskModelAvailability>
    suspend fun isCurrentLanguageModelAvailable(): Boolean
}

package com.grappim.aipal.android.recognition.factory

import com.grappim.aipal.android.recognition.android.AndroidSSTManager
import com.grappim.aipal.android.recognition.vosk.VoskSSTManager
import com.grappim.aipal.data.recognition.CurrentSTTManager
import com.grappim.aipal.data.recognition.STTManager
import org.lighthousegames.logging.logging

class SSTFactory(
    private val androidSSTManager: AndroidSSTManager,
    private val voskSSTManager: VoskSSTManager,
) {
    fun getSSTManager(currentSTTManager: CurrentSTTManager): STTManager {
        logging().d { "Now you are using: $currentSTTManager" }
        return when (currentSTTManager) {
            CurrentSTTManager.Android -> androidSSTManager
            CurrentSTTManager.Vosk -> voskSSTManager
        }
    }
}

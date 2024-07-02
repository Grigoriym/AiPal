package com.grappim.aipal.android.recognition.factory

import com.grappim.aipal.android.recognition.android.AndroidSSTManager
import com.grappim.aipal.android.recognition.vosk.VoskSttManager
import com.grappim.aipal.data.recognition.CurrentSTTManager
import com.grappim.aipal.data.recognition.STTManager
import org.lighthousegames.logging.logging

class STTFactory(
    private val androidSSTManagerFactory: () -> AndroidSSTManager,
    private val voskSttManagerFactory: () -> VoskSttManager
) {
    fun getSSTManager(currentSTTManager: CurrentSTTManager): STTManager {
        logging().d { "Now you are using: $currentSTTManager" }
        val manager = when (currentSTTManager) {
            CurrentSTTManager.Android -> androidSSTManagerFactory()
            CurrentSTTManager.Vosk -> voskSttManagerFactory()
        }
        logging().d { "Now you are using: $manager" }
        return manager
    }
}

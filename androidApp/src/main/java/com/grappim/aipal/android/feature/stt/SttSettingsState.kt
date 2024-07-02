package com.grappim.aipal.android.feature.stt

import com.grappim.aipal.android.files.vosk.VoskModelAvailability
import com.grappim.aipal.core.SupportedLanguage
import com.grappim.aipal.data.recognition.CurrentSTTManager

data class SttSettingsState(
    val currentSTTManager: CurrentSTTManager,
    val onSttManagerChange: (CurrentSTTManager) -> Unit,
    val description: String = "",
    val currentLanguage: SupportedLanguage,
    val onSetCurrentLanguage: (String) -> Unit,
    val languages: Set<String>,
    val availableModels: List<VoskModelUI> = emptyList(),
    val onModelDownload: (VoskModelAvailability) -> Unit,
    val isDownloading: Boolean = false
)

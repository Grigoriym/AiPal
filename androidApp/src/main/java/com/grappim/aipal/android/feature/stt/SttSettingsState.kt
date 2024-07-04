package com.grappim.aipal.android.feature.stt

import com.grappim.aipal.android.files.vosk.VoskModelAvailability
import com.grappim.aipal.core.LaunchedEffectResult
import com.grappim.aipal.core.SupportedLanguage
import com.grappim.aipal.data.recognition.CurrentSTTManager
import com.grappim.aipal.feature.chat.SnackbarData

data class SttSettingsState(
    val currentSTTManager: CurrentSTTManager,
    val onSttManagerChange: (CurrentSTTManager) -> Unit,
    val description: String = "",
    val currentLanguage: SupportedLanguage,
    val onSetCurrentLanguage: (String) -> Unit,
    val languages: Set<String>,
    val availableModels: List<VoskModelUI> = emptyList(),
    val onModelDownload: (VoskModelAvailability) -> Unit,
    val isDownloading: Boolean = false,
    val showAlertDialog: Boolean = false,
    val onDismissDialog: () -> Unit,
    val snackbarMessage: LaunchedEffectResult<SnackbarData> = LaunchedEffectResult(SnackbarData()),
    val acknowledgeError: () -> Unit
)

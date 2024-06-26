package com.grappim.aipal.feature.settings.apiKeys

import com.grappim.aipal.core.LaunchedEffectResult

data class ApiKeysState(
    val openAiApiKey: String = "",
    val onSetOpenAiApiKey: (String) -> Unit,
    val saveOpenAiApiKey: () -> Unit,
    val onCheckApiKey: () -> Unit,
    val snackbarMessage: LaunchedEffectResult<String> = LaunchedEffectResult(""),
    val onKeyClear: () -> Unit,
    val dismissSnackbar: () -> Unit
)

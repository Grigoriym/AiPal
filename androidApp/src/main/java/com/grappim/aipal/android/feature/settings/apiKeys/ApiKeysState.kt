package com.grappim.aipal.android.feature.settings.apiKeys

import com.grappim.aipal.android.core.LaunchedEffectResult

data class ApiKeysState(
    val openAiApiKey: String = "",
    val onSetOpenAiApiKey: (String) -> Unit,
    val saveOpenAiApiKey: () -> Unit,
    val onCheckApiKey: () -> Unit,
    val snackbarMessage: LaunchedEffectResult<String> = LaunchedEffectResult(""),
    val onKeyClear: () -> Unit,
    val dismissSnackbar: () -> Unit
)

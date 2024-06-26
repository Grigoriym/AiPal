package com.grappim.aipal.core

import kotlinx.datetime.Clock

data class LaunchedEffectResult<T>(
    val data: T,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)

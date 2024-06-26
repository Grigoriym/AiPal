package com.grappim.aipal.core

import java.time.Instant

data class LaunchedEffectResult<T>(
    val data: T,
    val timestamp: Long = Instant.now().toEpochMilli()
)

package com.grappim.aipal.android.core

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.dsl.module

@OptIn(ExperimentalSerializationApi::class)
val jsonModule =
    module {
        single {
            Json {
                isLenient = true
                prettyPrint = false
                ignoreUnknownKeys = true
                explicitNulls = false
            }
        }
    }

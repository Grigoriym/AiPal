package com.grappim.aipal.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.local.LocalDataStorageImpl
import com.grappim.aipal.data.repo.AiPalRepo
import com.grappim.aipal.data.repo.AiPalRepoImpl
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val appModule = module {
    includes(commonModule())
}

@OptIn(ExperimentalSerializationApi::class)
fun commonModule() = module {
    single {
        Json {
            isLenient = true
            prettyPrint = false
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }
}

fun repoModule() = module {
    single<AiPalRepo> { AiPalRepoImpl(get<LocalDataStorage>()) }
}

fun mobileLocalDataStorageModule() = module {
    single<LocalDataStorage> { LocalDataStorageImpl(get<DataStore<Preferences>>()) }
}

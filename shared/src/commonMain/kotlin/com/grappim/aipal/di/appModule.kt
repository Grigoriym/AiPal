package com.grappim.aipal.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.grappim.aipal.cache.DatabaseDriverFactory
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.local.LocalDataStorageImpl
import com.grappim.aipal.data.repo.AiPalRepo
import com.grappim.aipal.data.repo.AiPalRepoImpl
import com.grappim.aipal.data.repo.DbRepo
import com.grappim.aipal.data.repo.DbRepoImpl
import com.grappim.aipal.data.uuid.UuidGenerator
import com.grappim.aipal.data.uuid.UuidGeneratorImpl
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val appModule = module {
    includes(commonModule())
}

fun commonModule() = module {
    single { provideJson() }

    single<UuidGenerator> { UuidGeneratorImpl() }
}

fun repoModule() = module {
    single<AiPalRepo> {
        AiPalRepoImpl(
            localDataStorage = get<LocalDataStorage>(),
            uuidGenerator = get<UuidGenerator>(),
            dbRepo = get<DbRepo>()
        )
    }
    single<DbRepo> {
        DbRepoImpl(
            databaseDriverFactory = get<DatabaseDriverFactory>()
        )
    }
}

fun mobileLocalDataStorageModule() = module {
    single<LocalDataStorage> { LocalDataStorageImpl(get<DataStore<Preferences>>()) }
}

@OptIn(ExperimentalSerializationApi::class)
private fun provideJson() = Json {
    isLenient = true
    prettyPrint = false
    ignoreUnknownKeys = true
    explicitNulls = false
}

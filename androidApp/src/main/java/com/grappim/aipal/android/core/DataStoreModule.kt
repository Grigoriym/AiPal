package com.grappim.aipal.android.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.grappim.aipal.android.data.local.LocalDataStorageImpl
import com.grappim.aipal.data.local.LocalDataStorage
import org.koin.dsl.module

val dataStoreModule =
    module {
        single {
            val context = get<Context>()
            PreferenceDataStoreFactory.create(
                produceFile = {
                    context.preferencesDataStoreFile("aipal_datastore")
                },
            )
        }
        single<LocalDataStorage> { LocalDataStorageImpl(get<DataStore<Preferences>>()) }
    }

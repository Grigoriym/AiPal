package com.grappim.aipal.android.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.grappim.aipal.data.local.LocalDataStorageImpl
import com.grappim.aipal.createDataStore.getAndroidDataStore
import com.grappim.aipal.data.local.LocalDataStorage
import org.koin.dsl.module

val dataStoreModule =
    module {
        single<DataStore<Preferences>> {
            val context = get<Context>()
            getAndroidDataStore(context)
        }
        single<LocalDataStorage> { LocalDataStorageImpl(get<DataStore<Preferences>>()) }
    }

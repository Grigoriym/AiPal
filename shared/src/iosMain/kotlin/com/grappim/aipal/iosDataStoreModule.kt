package com.grappim.aipal

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import createIosDataStore
import org.koin.dsl.module

fun iosDataStoreModule() = module {
    single<DataStore<Preferences>> {
        createIosDataStore()
    }
}

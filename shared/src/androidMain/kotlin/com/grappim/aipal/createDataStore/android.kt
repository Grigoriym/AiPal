package com.grappim.aipal.createDataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.grappim.aipal.data.local.dataStoreFileName
import com.grappim.aipal.data.local.getDataStore

fun getAndroidDataStore(context: Context): DataStore<Preferences> = getDataStore(
    producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
)

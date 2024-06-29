package com.grappim.aipal.android.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.grappim.aipal.android.files.download.FileDownloader
import com.grappim.aipal.android.files.download.FileDownloaderImpl
import com.grappim.aipal.android.files.path.FolderPathManager
import com.grappim.aipal.android.files.path.FolderPathManagerImpl
import com.grappim.aipal.android.files.vosk.VoskModelCheck
import com.grappim.aipal.android.files.vosk.VoskModelCheckImpl
import com.grappim.aipal.android.files.zip.FileUnzipManager
import com.grappim.aipal.android.files.zip.FileUnzipManagerImpl
import com.grappim.aipal.createDataStore.getAndroidDataStore
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.local.LocalDataStorageImpl
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val dataStoreModule =
    module {
        single<DataStore<Preferences>> {
            val context = get<Context>()
            getAndroidDataStore(context)
        }
        single<LocalDataStorage> { LocalDataStorageImpl(get<DataStore<Preferences>>()) }
        single<FileDownloader> { FileDownloaderImpl() }
        single<FileUnzipManager> { FileUnzipManagerImpl() }
        single<FolderPathManager> { FolderPathManagerImpl(get<Context>()) }
        single<VoskModelCheck> { VoskModelCheckImpl(get<Json>()) }
    }

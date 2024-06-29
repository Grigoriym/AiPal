package com.grappim.aipal.android.files.path

import android.content.Context
import java.io.File

class FolderPathManagerImpl(
    private val appContext: Context,
) : FolderPathManager {
    override fun getCacheFolder(): File = appContext.cacheDir

    override fun getMainFolder(childFolder: String): File {
        val folder = File(appContext.filesDir, "aipal/$childFolder")
        if (folder.exists().not()) {
            folder.mkdirs()
        }
        return folder
    }

    override fun getVoskModelFolder(lang: String): File =
        getMainFolder("${lang}_voskModel")
}

package com.grappim.aipal.android.files.path

import java.io.File

interface FolderPathManager {
    fun getCacheFolder(): File

    fun getMainFolder(childFolder: String = ""): File

    fun getVoskModelFolder(lang: String): File
}

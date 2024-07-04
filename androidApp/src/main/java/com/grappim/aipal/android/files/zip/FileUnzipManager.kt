package com.grappim.aipal.android.files.zip

import java.io.File

interface FileUnzipManager {
    suspend fun unzip(
        zipFile: File,
        destDirectory: File,
        progressCallback: suspend (progress: Int) -> Unit
    )
}

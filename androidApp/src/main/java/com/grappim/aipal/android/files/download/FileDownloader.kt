package com.grappim.aipal.android.files.download

import java.io.File

interface FileDownloader {
    suspend fun downloadFile(
        link: String,
        fileToSave: File,
        tempFile: File,
        progressCallback: (progress: Int) -> Unit,
    )
}

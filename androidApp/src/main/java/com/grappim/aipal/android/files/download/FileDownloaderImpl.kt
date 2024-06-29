package com.grappim.aipal.android.files.download

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.prepareGet
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging
import java.io.File

class FileDownloaderImpl : FileDownloader {

    companion object {
        private val BUFFER_SIZE: Long = 1024 * 256
    }

    private val client = HttpClient()
    private val logging = logging()

    override suspend fun downloadFile(
        link: String,
        fileToSave: File,
        tempFile: File,
        progressCallback: (currentBytes: Long, totalBytes: Long) -> Unit,
    ) = withContext(Dispatchers.IO) {
        client.prepareGet(link).execute { httpResponse ->
            val channel: ByteReadChannel = httpResponse.body()
            val contentLength = httpResponse.contentLength() ?: -1L
            var currentBytes = 0L
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(BUFFER_SIZE)
                while (!packet.isEmpty) {
                    val bytes = packet.readBytes()
                    tempFile.appendBytes(bytes)
                    logging.d { "Received ${tempFile.length()} bytes from $contentLength" }
                    currentBytes += bytes.size
                    progressCallback(currentBytes, contentLength)
                }
            }
            if (tempFile.renameTo(fileToSave)) {
                logging.d { "File successfully moved from ${tempFile.path} to ${fileToSave.path}" }
            } else {
                logging.w { "Failed to rename file. Attempting to copy and delete." }
                tempFile.copyTo(fileToSave, overwrite = true)
                if (tempFile.delete()) {
                    logging.d { "Temp file deleted after copy" }
                } else {
                    logging.w { "Failed to delete temp file after copy" }
                }
            }
        }
    }
}

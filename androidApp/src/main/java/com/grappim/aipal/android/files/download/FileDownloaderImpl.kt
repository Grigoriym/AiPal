package com.grappim.aipal.android.files.download

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.prepareGet
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import org.lighthousegames.logging.logging
import java.io.File
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class FileDownloaderImpl : FileDownloader {

    companion object {
        private const val BUFFER_SIZE: Long = 1024 * 256
    }

    private val client = HttpClient()
    private val logging = logging()

    override suspend fun downloadFile(
        link: String,
        fileToSave: File,
        tempFile: File,
        progressCallback: suspend (progress: Int) -> Unit,
    ) {
        try {
            client.prepareGet(link).execute { httpResponse ->
                if (!httpResponse.status.isSuccess()) {
                    throw IOException("HTTP error ${httpResponse.status.value}")
                }

                val channel: ByteReadChannel = httpResponse.body()
                val contentLength = httpResponse.contentLength() ?: -1L
                var currentBytes = 0L

                tempFile.outputStream().use { outputStream ->
                    while (!channel.isClosedForRead) {
                        val packet = channel.readRemaining(BUFFER_SIZE)
                        while (!packet.isEmpty) {
                            val bytes = packet.readBytes()
                            outputStream.write(bytes)
                            currentBytes += bytes.size
                            val progress =
                                if (contentLength > 0) (currentBytes * 100 / contentLength) else 0
                            progressCallback(progress.toInt())
                        }
                    }
                }

                while (!channel.isClosedForRead) {
                    val packet = channel.readRemaining(BUFFER_SIZE)
                    while (!packet.isEmpty) {
                        val bytes = packet.readBytes()
                        tempFile.appendBytes(bytes)
                        logging.d { "Received ${tempFile.length()} bytes from $contentLength" }
                        currentBytes += bytes.size
                        val progress =
                            if (contentLength > 0) (currentBytes * 100 / contentLength) else 0
                        progressCallback(progress.toInt())
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logging.e(e) { "Error downloading file from $link" }
            tempFile.delete()
            throw e
        }
    }
}

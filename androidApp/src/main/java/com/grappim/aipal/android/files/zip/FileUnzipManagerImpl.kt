package com.grappim.aipal.android.files.zip

import org.lighthousegames.logging.logging
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipInputStream
import kotlin.coroutines.cancellation.CancellationException

class FileUnzipManagerImpl : FileUnzipManager {

    companion object {
        private const val BUFFER_SIZE = 1024 * 8
    }

    private val logging = logging()

    override suspend fun unzip(
        zipFile: File,
        destDirectory: File,
        progressCallback: suspend (progress: Int) -> Unit
    ) {
        if (!zipFile.exists()) {
            throw FileNotFoundException("Zip file does not exist: ${zipFile.path}")
        }

        if (!destDirectory.exists() && !destDirectory.mkdirs()) {
            throw IOException("Failed to create destination directory: ${destDirectory.path}")
        }

        progressCallback(0)

        val destDirPath = destDirectory.canonicalPath
        val totalSize = zipFile.length()
        var processedSize = 0L

        try {
            ZipInputStream(zipFile.inputStream().buffered()).use { zipIn ->
                var entry = zipIn.nextEntry
                var rootFolder: String? = null

                while (entry != null) {
                    // Determine the root folder if not already set
                    if (rootFolder == null && entry.name.contains("/")) {
                        rootFolder = entry.name.substringBefore("/")
                    }

                    // Skip the root folder
                    val relativeFilePath =
                        if (rootFolder != null && entry.name.startsWith(rootFolder)) {
                            entry.name.substring(rootFolder.length + 1)
                        } else {
                            entry.name
                        }

                    // Skip empty paths (which would be the root folder entry itself)
                    if (relativeFilePath.isNotEmpty()) {
                        val filePath = File(destDirectory, relativeFilePath).canonicalPath

                        // Check for Zip Slip
                        if (!filePath.startsWith(destDirPath + File.separator)) {
                            throw SecurityException("Entry is outside of the target directory: ${entry.name}")
                        }

                        if (!entry.isDirectory) {
                            // If the entry is a file, extract it
                            extractFile(zipIn, filePath)
                            processedSize += entry.compressedSize
                            val percentage =
                                if (totalSize > 0) (processedSize * 100 / totalSize) else 0
                            val result = percentage.coerceAtMost(100).toInt()
                            logging.d { "unzipping :${result}" }
                            progressCallback(result)
                        } else {
                            // If the entry is a directory, make the directory
                            val dir = File(filePath)
                            if (!dir.exists() && !dir.mkdirs()) {
                                throw IOException("Failed to create directory: ${dir.path}")
                            }
                        }
                    }

                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logging.e(e) { "Error unzipping file: ${zipFile.path}" }
            destDirectory.deleteRecursively()
            throw e
        } finally {
            if (!zipFile.delete()) {
                logging.w { "Failed to delete zip file: ${zipFile.path}" }
            }
        }
    }

    private fun extractFile(
        zipIn: ZipInputStream,
        filePath: String
    ) {
        val file = File(filePath)
        file.parentFile?.let {
            if (!it.exists() && !it.mkdirs()) {
                throw IOException("Failed to create parent directories for: ${file.path}")
            }
        }

        FileOutputStream(file).use { fos ->
            val buffer = ByteArray(BUFFER_SIZE)
            var length: Int
            while (zipIn.read(buffer).also { length = it } >= 0) {
                fos.write(buffer, 0, length)
            }
        }
    }
}

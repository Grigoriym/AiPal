package com.grappim.aipal.android.files.zip

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

class FileUnzipManagerImpl : FileUnzipManager {

    companion object {
        private const val BUFFER_SIZE = 1024 * 256
    }

    private val logging = logging()

    override suspend fun unzip(
        zipFile: File,
        destDirectory: File
    ): Unit = withContext(Dispatchers.IO) {
        if (!destDirectory.exists()) {
            destDirectory.mkdirs()
        }
        val destDirPath = destDirectory.canonicalPath

        ZipInputStream(zipFile.inputStream()).use { zipIn ->
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
                    } else {
                        // If the entry is a directory, make the directory
                        val dir = File(filePath)
                        dir.mkdirs()
                    }
                }
                logging.d { "unzipping" }
                zipIn.closeEntry()
                entry = zipIn.nextEntry
            }
        }

        zipFile.delete()
    }

    private fun extractFile(zipIn: ZipInputStream, filePath: String) {
        File(filePath).parentFile?.mkdirs() // Create parent directories if they don't exist
        FileOutputStream(filePath).use { fos ->
            val buffer = ByteArray(BUFFER_SIZE)
            var length = zipIn.read(buffer)
            while (length >= 0) {
                fos.write(buffer, 0, length)
                length = zipIn.read(buffer)
            }
        }
    }
}

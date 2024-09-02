package de.ogwars.ogcloud.util

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

class ZipUtil {
    companion object {
        fun unzipFile(zipFilePath: String, destinationPath: String) {
            val zipFile = ZipFile(zipFilePath)
            val destDir = File(destinationPath)

            if (!destDir.exists()) {
                destDir.mkdirs()
            }

            zipFile.use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    val entryDestination = File(destDir, entry.name)

                    if (entry.isDirectory) {
                        entryDestination.mkdirs()
                    } else {
                        entryDestination.parentFile?.mkdirs()

                        zip.getInputStream(entry).use { input ->
                            FileOutputStream(entryDestination).use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.github.xingray.kotlin.file

import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class UnZipFile(private val zipFile: File) {

    /**
     * 1. create a dir in targetDir
     * 2. unzip content in zip file into created dir
     *
     * test.zip
     *  /01.txt
     *  /dir-01
     *      /02.txt
     *
     *  unzipAsDir /usr/root/tmp
     *
     *  result:
     *  /usr/root/tmp/test/
     *                  /01.txt
     *                  /dir-01
     *                      /02.txt
     */
    fun unZipAsDir(targetDir: File) {
        unzipAsDir(zipFile, targetDir)
    }

    /**
     *
     * unzip content in zip file into target dir
     *
     * test.zip
     *  /01.txt
     *  /dir-01
     *      /02.txt
     *
     *  unzipAsDir /usr/root/tmp
     *
     *  result:
     *  /usr/root/tmp/
     *               /01.txt
     *               /dir-01
     *                  /02.txt
     */
    fun unZipToDir(targetDir: File) {
        unzipToDir(zipFile, targetDir)
    }

    fun <T> readEntry(entryName: String, consumer: (ZipInputStream) -> T): T? {
        return ZipInputStream(zipFile.inputStream()).use { zipInputStream ->
            var entry: ZipEntry? = zipInputStream.nextEntry
            while (entry != null) {
                if (entry.name == entryName) {
                    return@use consumer.invoke(zipInputStream)
                }
                zipInputStream.closeEntry()
                entry = zipInputStream.nextEntry
            }
            return null
        }
    }

    fun readString(entryName: String): String? {
        return readEntry(entryName) {
            it.bufferedReader(Charsets.UTF_8).readText()
//            it.readAllBytes().toString(Charsets.UTF_8)
        }
    }

    fun readAsFile(entryName: String, targetFile: File) {
        readEntry(entryName) { zipInputStream ->
            FileOutputStream(targetFile).use { outputStream ->
                val buffer = ByteArray(1024)
                var len = zipInputStream.read(buffer)
                while (len != -1) {
                    outputStream.write(buffer, 0, len)
                    len = zipInputStream.read(buffer)
                }
            }
        }
    }
}

fun unzipToDir(zipFile: File, destinationDir: File) {
    ZipInputStream(zipFile.inputStream()).use { zipInputStream ->
        var entry: ZipEntry? = zipInputStream.nextEntry
        while (entry != null) {
            val entryFile = File(destinationDir, entry.name)
            if (entry.isDirectory) {
                entryFile.mkdirs()
            } else {
                val parentDir = entryFile.parentFile
                if (parentDir != null) {
                    if (!parentDir.exists()) {
                        parentDir.mkdirs()
                    }
                }
                FileOutputStream(entryFile).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var len = zipInputStream.read(buffer)
                    while (len != -1) {
                        outputStream.write(buffer, 0, len)
                        len = zipInputStream.read(buffer)
                    }
                }
            }
            zipInputStream.closeEntry()
            entry = zipInputStream.nextEntry
        }
    }
}

fun unzipAsDir(zipFile: File, destinationDir: File) {
    ZipInputStream(zipFile.inputStream()).use { zipInputStream ->
        var entry: ZipEntry? = zipInputStream.nextEntry
        while (entry != null) {
            val dir = destinationDir.resolve(zipFile.nameWithoutExtension)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val entryFile = File(dir, entry.name)
            if (entry.isDirectory) {
                entryFile.mkdirs()
            } else {
                val parentDir = entryFile.parentFile
                if (parentDir != null) {
                    if (!parentDir.exists()) {
                        parentDir.mkdirs()
                    }
                }
                FileOutputStream(entryFile).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var len = zipInputStream.read(buffer)
                    while (len != -1) {
                        outputStream.write(buffer, 0, len)
                        len = zipInputStream.read(buffer)
                    }
                }
            }
            zipInputStream.closeEntry()
            entry = zipInputStream.nextEntry
        }
    }
}

fun readTxtFromZip(zipFile: File, fileName: String): String? {
    var result: String? = null
    ZipInputStream(zipFile.inputStream()).use { zipInputStream ->
        var entry: ZipEntry? = zipInputStream.nextEntry
        while (entry != null) {
            if (entry.name == fileName) {
                result = readTextFromStream(zipInputStream)
                break
            }
            entry = zipInputStream.nextEntry
        }
    }
    return result
}

private fun readTextFromStream(inputStream: ZipInputStream): String {
    val reader = BufferedReader(InputStreamReader(inputStream))
    val stringBuilder = StringBuilder()
    var line: String?
    while (reader.readLine().also { line = it } != null) {
        stringBuilder.append(line).append("\n")
    }
    return stringBuilder.toString()
}

fun File.unzipAllZipFiles(targetDir: File? = null, deleteZipFile:Boolean = false) {
    if (!this.isExistDir()) {
        throw IllegalArgumentException("file is not exists or not a dir, path:${this.absolutePath}")
    }
    val srcDir = this
    val zipFiles = srcDir.listFiles { f -> f.exists() && f.isFile && f.name.endsWith(".zip") }
    if (zipFiles == null) {
        // no .zip file exists
        return
    }
    val targetDirectory = targetDir ?: this
    targetDirectory.ensureDirExists()

    zipFiles.forEachIndexed { index, file ->
        val unZip = UnZipFile(file)
        unZip.unZipAsDir(targetDirectory)
        if (deleteZipFile) {
            file.delete()
        }
    }
}
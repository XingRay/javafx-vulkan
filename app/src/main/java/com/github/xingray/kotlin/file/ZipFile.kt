package com.github.xingray.kotlin.file

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ZipFile() {

    private val zipSourceList = mutableListOf<ZipSource>()

    fun addFile(zipName: String, file: File) {
        zipSourceList.add(FileZipSource(zipName, file))
    }

    fun addDir(dir: File) {
        val fileZipSourceList = dir.fileTreeToList().map {
            val parentFile = dir.absoluteFile.parentFile
            val startIndex = if (parentFile == null) {
                0
            } else {
                parentFile.absolutePath.length + 1
            }
            val zipName = it.absolutePath.substring(startIndex)
            FileZipSource(zipName, it)
        }
        zipSourceList.addAll(fileZipSourceList)
    }

    fun addDirSubFiles(dir: File) {
        val fileZipSourceList = dir.fileTreeToList().map {
            val zipName = it.absolutePath.substring(dir.absoluteFile.absolutePath.length + 1)
            FileZipSource(zipName, it)
        }
        zipSourceList.addAll(fileZipSourceList)
    }

    fun addString(zipName: String, content: String) {
        zipSourceList.add(StringZipSource(zipName, content))
    }

    fun addByteArray(zipName: String, byteArray: ByteArray) {
        zipSourceList.add(ByteArrayZipSource(zipName, byteArray))
    }

    fun addZipSource(zipSource: ZipSource) {
        zipSourceList.add(zipSource)
    }

    fun addZipSources(zipSources: List<ZipSource>) {
        zipSourceList.addAll(zipSources)
    }

    fun writeTo(zipFile: File) {
        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zipOutputStream ->
            for (zipSource in zipSourceList) {
                zipSource.write(zipOutputStream)
            }
        }
    }
}

interface ZipSource {
    fun write(zipOutputStream: ZipOutputStream)
}

class FileZipSource(private val zipName: String, private val file: File) : ZipSource {
    override fun write(zipOutputStream: ZipOutputStream) {
        zipFile(zipName, file, zipOutputStream)
    }
}

class StringZipSource(private val zipName: String, private val content: String) : ZipSource {
    override fun write(zipOutputStream: ZipOutputStream) {
        val byteArray = content.toByteArray(Charsets.UTF_8)
        zipByteArray(zipName, byteArray, zipOutputStream)
    }
}

class ByteArrayZipSource(private val zipName: String, private val byteArray: ByteArray) : ZipSource {
    override fun write(zipOutputStream: ZipOutputStream) {
        zipByteArray(zipName, byteArray, zipOutputStream)
    }
}

private fun zipFile(zipName: String, file: File, zipOutputStream: ZipOutputStream) {
    val entry = ZipEntry(zipName)
    zipOutputStream.putNextEntry(entry)
    FileInputStream(file).use { inputStream ->
        inputStream.copyTo(zipOutputStream)
    }
    zipOutputStream.closeEntry()
}

private fun zipByteArray(zipName: String, byteArray: ByteArray, zipOutputStream: ZipOutputStream) {
    val entry = ZipEntry(zipName)
    zipOutputStream.putNextEntry(entry)
    zipOutputStream.write(byteArray)
    zipOutputStream.closeEntry()
}

//fun zip(fileList: List<File>, outputFile: File) {
//    if (fileList.isEmpty()) {
//        return
//    }
//    ZipOutputStream(BufferedOutputStream(FileOutputStream(outputFile))).use { zipOutputStream ->
//        for (file in fileList) {
//            if (!file.exists()) {
//                continue
//            }
//            if (file.isFile) {
//                zipFile(file.absoluteFile, file.absoluteFile.parentFile, zipOutputStream)
//            } else if (file.isDirectory) {
//                zipDir(file.absoluteFile, file.absoluteFile.parentFile, zipOutputStream)
//            }
//        }
//
//        zipText("this is test", "readme.txt", zipOutputStream)
//    }
//}

/**
 * 将目录下的所有文件打包成zip文件, zip文件中包含源目录
 */
//fun zipDirectory2(sourceDir: File, outputFile: File) {
//    ZipOutputStream(BufferedOutputStream(FileOutputStream(outputFile))).use { zipOutputStream ->
//        zipDir(sourceDir.absoluteFile, sourceDir.absoluteFile.parentFile, zipOutputStream)
//    }
//}

/**
 * 将目录下的所有文件打包成zip文件, zip文件中包含源目录中的子文件, 不包含源目录
 */
//fun zipDirectory(sourceDir: File, outputFile: File) {
//    ZipOutputStream(BufferedOutputStream(FileOutputStream(outputFile))).use { zipOutputStream ->
//        zipDir(sourceDir, sourceDir, zipOutputStream)
//    }
//}

//private fun zipDir(directory: File, base: File, zipOutputStream: ZipOutputStream) {
//    val files = directory.listFiles() ?: return
//    for (file in files) {
//        if (file.isDirectory) {
//            zipDir(file, base, zipOutputStream)
//        } else {
//            zipFile(file, base, zipOutputStream)
//        }
//    }
//}

//private fun zipFile(file: File, base: File, zipOutputStream: ZipOutputStream) {
//    val zipName = file.path.substring(base.path.length + 1)
//    zipFile(file, zipName, zipOutputStream)
//}
//
//
//
//private fun zipText(text: String, zipName: String, zipOutputStream: ZipOutputStream) {
//    val byteArray = text.toByteArray(Charsets.UTF_8)
//    zipByteArray(byteArray, zipName, zipOutputStream)
//}
package com.github.xingray.kotlin.file

import com.github.xingray.kotlin.readFloatArray
import com.github.xingray.kotlin.readIntArray
import java.io.File
import java.util.*

fun File.fileTreeToList(predicate: (File) -> Boolean = { it.exists() && it.isFile }): List<File> {
    if (!this.exists()) {
        return emptyList()
    }
    if (this.isFile) {
        return listOf(this)
    } else if (this.isDirectory) {
        val queue = LinkedList<File>()
        queue.add(this)
        val list = mutableListOf<File>()

        do {
            val file = queue.removeFirst() ?: break

            if (predicate.invoke(file)) {
                list.add(file)
            }

            if (file.isDirectory) {
                file.listFiles()?.let {
                    queue.addAll(it.asList())
                }
            }
        } while (!queue.isEmpty())

        return list;
    } else {
        return emptyList()
    }
}

fun File.ensureDirExists(): File {
    if (!exists()) {
        mkdirs()
        return this
    }
    if (isFile) {
        delete()
        mkdirs()
    }
    return this
}

fun File.ensureFileExists(): File {
    parentFile?.ensureDirExists()
    if (exists()) {
        if (isDirectory) {
            deleteRecursively()
            createNewFile()
        }
    } else {
        createNewFile()
    }
    return this
}

fun File.ensureDirExistsAndEmpty(): File {
    ensureDirExists()
    deleteFilesAndSubdirectories()
    return this
}

fun File.deleteFilesAndSubdirectories(): File {
    val directory = this
    val files = directory.listFiles()
    if (!files.isNullOrEmpty()) {
        for (file in files) {
            if (file.isDirectory) {
                file.deleteFilesAndSubdirectories()
            } else {
                file.delete()
            }
        }
    }

    return this
}

fun File.readFloatArray(numbersPerLine: Int = 2): FloatArray {
    val reader = reader(Charsets.UTF_8)
    reader.use {
        return it.readFloatArray(numbersPerLine)
    }
}

fun File.readIntArray(numbersPerLine: Int = 3): IntArray {
    val reader = reader(Charsets.UTF_8)
    reader.use {
        return it.readIntArray(numbersPerLine)
    }
}

fun File.relativePathToAbsolutePath(relativePath: String): String {
    return this.resolve(relativePath).absolutePath
}

fun File?.isExistFile(): Boolean {
    return this != null && this.exists() && this.isFile
}

fun File?.isExistDir(): Boolean {
    return this != null && this.exists() && this.isDirectory
}

fun List<File>.isAllExistFile(): Boolean {
    return all { it.isExistFile() }
}

fun List<File>.isAllExistDir(): Boolean {
    return all { it.isExistDir() }
}
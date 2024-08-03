package com.github.xingray.android.resource

import java.io.File
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * 压缩文件内部的一个文件
 */
class ZipFileResource(private val zipFile: File, private val entryName: String) : Resource {

    override fun <R> use(consumer: (InputStream) -> R): R? {
        return ZipInputStream(zipFile.inputStream()).use { zipInputStream ->
            var r: R? = null
            var entry: ZipEntry? = zipInputStream.nextEntry
            while (entry != null) {
                if (entry.name == entryName) {
                    r = consumer.invoke(zipInputStream)
                    zipInputStream.closeEntry()
                    break
                }
                zipInputStream.closeEntry()
                entry = zipInputStream.nextEntry
            }
            r
        }
    }
}
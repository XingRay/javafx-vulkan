package com.github.xingray.android.resource

import java.io.File
import java.io.InputStream

/**
 * 本地文件
 */
class FileResource(val file: File) : Resource {

    override fun <T> use(consumer: (InputStream) -> T): T? {
        return file.takeIf { it.exists() && it.isFile }?.inputStream()?.use(consumer)
    }
}
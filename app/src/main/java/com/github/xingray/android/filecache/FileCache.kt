package com.github.xingray.android.filecache

import com.github.xingray.kotlin.file.ensureDirExists
import java.io.File

class FileCache(val dir: File) : Cache {

    override fun getString(key: String, defaultValue: String?): String? {
        val file = dir.resolve(key)
        if (!file.exists() || !file.isFile) {
            return null
        }

        return file.readText(Charsets.UTF_8)
    }

    override fun putString(key: String, value: String) {
        dir.ensureDirExists()
        val file = dir.resolve(key)
        file.writeText(value, Charsets.UTF_8)
    }
}
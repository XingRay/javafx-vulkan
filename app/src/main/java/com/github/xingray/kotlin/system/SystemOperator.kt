package com.github.xingray.kotlin.system

import java.io.File

private const val TAG = "SystemExtension"

object SystemOperator {
    fun getUserHome(): File {
        return File(System.getProperty("user.home"))
    }

    fun openDir(dir:File){
        Runtime.getRuntime().exec(arrayOf("explorer.exe", dir.absolutePath))
    }
}

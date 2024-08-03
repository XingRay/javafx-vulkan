package com.github.xingray.kotlin

import android.util.Log

import com.github.xingray.kotlin.file.ensureDirExists
import java.io.File

class NativeLibraryLoader {
    companion object {

        private val TAG = NativeLibraryLoader::class.java.simpleName

        fun loadLibrary(saveDir: File, libPath: String) {
            Log.d(TAG, "NativeLibraryLoader#loadLibrary: libPath:$libPath")
            val libFile = File(libPath)
            if (libFile.exists()) {
                System.load(libFile.getAbsolutePath());
                return
            }

            val targetLibFile = saveDir.resolve(libPath)
            Log.d(TAG, "loadLibrary: targetLibFile:${targetLibFile}")
            val resource = NativeLibraryLoader::class.java.classLoader.getResourceAsStream(libPath) ?: let {
                throw IllegalArgumentException("can not read resource, libPath:${libPath}")
            }
            val dir = targetLibFile.parentFile
            dir.ensureDirExists()
            Log.d(TAG, "loadLibrary: dir:${dir.absolutePath}")

            targetLibFile.outputStream().use { resource.copyTo(it) }
            Log.d(TAG, "loadLibrary: System.load: ${targetLibFile.getAbsolutePath()}")
            System.load(targetLibFile.getAbsolutePath())
            Log.d(TAG, "loadLibrary finish: System.load: ${targetLibFile.getAbsolutePath()}")
        }
    }
}
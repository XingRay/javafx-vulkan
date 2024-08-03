package com.github.xingray.android.resource

import android.util.Log
import java.io.InputStream

class JarInnerResource(val path: String) : Resource {

    private val TAG = JarInnerResource::class.java.simpleName

    override fun <R> use(consumer: (InputStream) -> R): R? {
        val inputStream = JarInnerResource::class.java.classLoader.getResourceAsStream(path) ?: let {
            Log.d(TAG, "JarInnerResource use: can not find $path in jar")
            return null
        }
        return consumer.invoke(inputStream)
    }
}
package com.github.xingray.android.resource

import java.io.ByteArrayInputStream
import java.io.InputStream

class ByteArrayResource(private val byteArray: ByteArray) : Resource {
    override fun <R> use(consumer: (InputStream) -> R): R? {
        return ByteArrayInputStream(byteArray).use(consumer)
    }
}
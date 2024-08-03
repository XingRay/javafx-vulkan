package com.github.xingray.android.resource

import java.io.InputStream
import java.net.URI

/**
 * Java uri, 例如通过 http 协议指定的远程文件
 */
class JavaUriResource(val uri: URI) : Resource {

    override fun <R> use(consumer: (InputStream) -> R): R? {
        TODO("Not yet implemented")
    }
}
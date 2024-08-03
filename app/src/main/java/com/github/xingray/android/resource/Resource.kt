package com.github.xingray.android.resource

import java.io.InputStream

/**
 * 远程资源或者本地资源, 文件等最终可以读取成二进制数据
 */
interface Resource {
    fun <R> use(consumer: (InputStream) -> R): R?
}
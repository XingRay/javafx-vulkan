package com.github.xingray.kotlin

import java.util.UUID

private val TAG = "UUIDExtension"

fun UUID.hexString(): String {
    val uuid = this
    val mostSignificantBits: Long = uuid.getMostSignificantBits()
    val leastSignificantBits: Long = uuid.getLeastSignificantBits()

    // 使用位运算组合最高位和最低位
    val combinedBits = mostSignificantBits shl 32 or (leastSignificantBits and 0xFFFFFFFFL)

    // 将结果转换为十六进制字符串
    return java.lang.Long.toHexString(combinedBits)
}
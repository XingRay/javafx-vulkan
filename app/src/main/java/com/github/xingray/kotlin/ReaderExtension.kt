package com.github.xingray.kotlin

import java.io.Reader

fun Reader.readIntArray(numbersPerLine: Int = 3): IntArray {
    // 从asset文件中读取所有行
    val lines = readLines()

    // 初始化IntArray，假设文件中的数字是正确的
    val intArray = IntArray(lines.size * numbersPerLine)

    // 解析每一行的数字
    var index = 0
    for (line in lines) {
        val values = line.split(",").filter { it.isNotEmpty() }.map { it.trim().toInt() }
        for (i in 0 until numbersPerLine) {
            intArray[index++] = values[i]
        }
    }

    return intArray
}

fun Reader.readFloatArray(numbersPerLine: Int = 2): FloatArray {
    // 从asset文件中读取所有行
    val lines = readLines()

    // 初始化IntArray，假设文件中的数字是正确的
    val array = FloatArray(lines.size * numbersPerLine)

    // 解析每一行的数字
    var index = 0
    for (line in lines) {
        val values = line.split(",").filter { it.isNotEmpty() }.map { it.trim().toFloat() }
        for (i in 0 until numbersPerLine) {
            array[index++] = values[i]
        }
    }

    return array
}
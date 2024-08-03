package com.github.xingray.kotlin

import com.github.xingray.kotlin.file.ensureDirExists
import java.io.File

fun FloatArray.swap(i: Int, j: Int) {
    val temp = this[i]
    this[i] = this[j]
    this[j] = temp
}


fun FloatArray.saveAs(file: File) {
    val dir = file.parentFile
    dir?.ensureDirExists()

    // 获取文件的写入流
    val writer = file.bufferedWriter()

    try {
        // 遍历IntArray中的数据
        for (i in this.indices step 2) {
            // 写入每行的数据，以逗号分隔
            writer.write("${this[i]}, ${this[i + 1]},")
            writer.newLine() // 换行
        }
    } finally {
        // 关闭流
        writer.close()
    }
}

fun FloatArray.toLinesString(countPerLine: Int = 2): String {
    // 获取文件的写入流
    val stringBuilder = StringBuilder()

    // 遍历IntArray中的数据
    for (i in this.indices step countPerLine) {
        // 写入每行的数据，以逗号分隔
        for (j in 0 until countPerLine) {
            stringBuilder.append("${this[i + j]},")
        }
        stringBuilder.appendLine() // 换行
    }
    return stringBuilder.toString()
}

fun FloatArray?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}

fun FloatArray?.hasElement(): Boolean {
    return this != null && this.isNotEmpty()
}

fun FloatArray?.ifHasElement(task: (FloatArray) -> Unit): FloatArray? {
    if (this != null && this.isNotEmpty()) {
        task.invoke(this)
    }
    return this
}

fun FloatArray?.concat(value: Float): FloatArray {
    if (this == null) {
        return floatArrayOf(value)
    }
    val size = this.size
    if (size == 0) {
        return floatArrayOf(value)
    }
    val newArray = FloatArray(size + 1)
    System.arraycopy(this, 0, newArray, 0, size)
    newArray[newArray.size - 1] = value
    return newArray
}

fun FloatArray?.concat(value: FloatArray?): FloatArray {
    if (this == null || isEmpty()) {
        return value?.copyOf() ?: floatArrayOf()
    }
    if (value == null || value.isEmpty()) {
        return this.copyOf()
    }

    val newArray = FloatArray(size + value.size)
    System.arraycopy(this, 0, newArray, 0, size)
    System.arraycopy(value, 0, newArray, size, value.size)
    return newArray
}

fun FloatArray?.concatAtStart(value: Float): FloatArray {
    if (this == null || isEmpty()) {
        return floatArrayOf(value)
    }
    val size = this.size
    val newArray = FloatArray(size + 1)
    newArray[0] = value
    System.arraycopy(this, 0, newArray, 1, size)
    return newArray
}

fun FloatArray?.concatAtStart(value: FloatArray?): FloatArray {
    if (this == null || isEmpty()) {
        return value?.copyOf() ?: floatArrayOf()
    }
    if (value == null || value.isEmpty()) {
        return this.copyOf()
    }

    val newArray = FloatArray(size + value.size)
    System.arraycopy(value, 0, newArray, 0, value.size)
    System.arraycopy(this, 0, newArray, value.size, size)
    return newArray
}

fun FloatArray.trimStart(elementCount: Int): FloatArray {
    val newSize = this.size - elementCount
    val newArray = FloatArray(newSize)
    System.arraycopy(this, elementCount, newArray, 0, newSize)
    return newArray
}

fun FloatArray.trimEnd(elementCount: Int): FloatArray {
    val newSize = this.size - elementCount
    val newArray = FloatArray(newSize)
    System.arraycopy(this, 0, newArray, 0, newSize)
    return newArray
}
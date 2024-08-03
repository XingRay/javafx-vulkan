package com.github.xingray.kotlin

fun IntArray?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}

fun IntArray?.concat(value: Int): IntArray {
    if (this == null) {
        return intArrayOf(value)
    }
    val size = this.size
    if (size == 0) {
        return intArrayOf(value)
    }
    val newIntArray = IntArray(size + 1)
    System.arraycopy(this, 0, newIntArray, 0, size)
    newIntArray[newIntArray.size - 1] = value
    return newIntArray
}

fun IntArray?.concat(value: IntArray?): IntArray {
    if (this == null || isEmpty()) {
        return value?.copyOf() ?: intArrayOf()
    }
    if (value == null || value.isEmpty()) {
        return this.copyOf()
    }

    val newIntArray = IntArray(size + value.size)
    System.arraycopy(this, 0, newIntArray, 0, size)
    System.arraycopy(value, 0, newIntArray, size, value.size)
    return newIntArray
}

fun IntArray?.concatAtStart(value: Int): IntArray {
    if (this == null || isEmpty()) {
        return intArrayOf(value)
    }
    val size = this.size
    val newIntArray = IntArray(size + 1)
    newIntArray[0] = value
    System.arraycopy(this, 0, newIntArray, 1, size)
    return newIntArray
}

fun IntArray?.concatAtStart(value: IntArray?): IntArray {
    if (this == null || isEmpty()) {
        return value?.copyOf() ?: intArrayOf()
    }
    if (value == null || value.isEmpty()) {
        return this.copyOf()
    }

    val newIntArray = IntArray(size + value.size)
    System.arraycopy(value, 0, newIntArray, 0, value.size)
    System.arraycopy(this, 0, newIntArray, value.size, size)
    return newIntArray
}

//fun IntArray.concat(value: Int): IntArray {
//    val size = this.size
//    if (size == 0) {
//        return intArrayOf(value)
//    }
//    val newIntArray = IntArray(size + 1)
//    System.arraycopy(this, 0, newIntArray, 0, size)
//    newIntArray[newIntArray.size - 1] = value
//    return newIntArray
//}

fun IntArray?.hasElement(): Boolean {
    return this != null && this.isNotEmpty()
}

fun IntArray.trimStart(elementCount: Int): IntArray {
    val newSize = this.size - elementCount
    val newIntArray = IntArray(newSize)
    System.arraycopy(this, elementCount, newIntArray, 0, newSize)
    return newIntArray
}

fun IntArray.trimEnd(elementCount: Int): IntArray {
    val newSize = this.size - elementCount
    val newIntArray = IntArray(newSize)
    System.arraycopy(this, 0, newIntArray, 0, newSize)
    return newIntArray
}

fun IntArray?.safetyGet(index: Int, defaultValue: Int): Int {
    if (this == null || isEmpty() || index >= size) {
        return defaultValue
    }
    return this[index]
}

fun IntArray.toLinesString(countPerLine: Int = 3): String {
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
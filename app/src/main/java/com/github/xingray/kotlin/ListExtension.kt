package com.github.xingray.kotlin

fun List<Any>?.hasElement(): Boolean {
    if (this == null) {
        return false
    }
    return !isEmpty()
}

fun List<Any>?.ifNullOrEmpty(task: () -> Unit) {
    if (this == null || this.isEmpty()) {
        task.invoke()
    }
}

fun <T> List<T>?.emptyToNull(): List<T>? {
    return if (this.isNullOrEmpty()) {
        null
    } else {
        this
    }
}

inline fun <T> List<T>.indexOfFirstWithIndex(predicate: (T, Int) -> Boolean): Int {
    var index = 0
    for (item in this) {
        if (predicate(item, index))
            return index
        index++
    }
    return -1
}

fun <T> List<T>.contentToString(stringMapper: ((T) -> String)? = null): String {
    val stringBuilder = StringBuilder()
    stringBuilder.append('[')
    for ((index, item) in this.withIndex()) {
        if (index > 0) {
            stringBuilder.append(',').append(' ')
        }
        stringBuilder.append(stringMapper?.invoke(item) ?: item.toString())
    }
    stringBuilder.append(']')
    return stringBuilder.toString()
}

fun <T> List<T>.contentToStringWithBuilder(stringMapper: ((T, StringBuilder) -> Unit)? = null): String {
    val stringBuilder = StringBuilder()
    stringBuilder.append('[')
    for ((index, item) in this.withIndex()) {
        if (index > 0) {
            stringBuilder.append(',').append(' ')
        }
        stringMapper?.invoke(item, stringBuilder) ?: stringBuilder.append(item.toString())
    }
    stringBuilder.append(']')
    return stringBuilder.toString()
}
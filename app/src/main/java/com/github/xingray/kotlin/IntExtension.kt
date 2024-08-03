package com.github.xingray.kotlin

import kotlin.math.max
import kotlin.math.min

fun Int.toBoolean(): Boolean {
    return this != 0
}

fun Int.rangeAsList(start: Int = 0) = IntArray(this) { index -> index + start }.toList()

fun Int.clamp(from: Int, to: Int): Int {
    val minValue = min(from, to)
    val maxValue = max(from, to)
    return if (this < minValue) {
        minValue
    } else if (this > maxValue) {
        maxValue
    } else {
        this
    }
}
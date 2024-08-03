package com.github.xingray.kotlin

import kotlin.math.abs

private val TAG = "FloatExtension"
fun Float.clamp(from: Float, to: Float): Float {
    val min = Math.min(from, to)
    val max = Math.max(from, to)

    return if (this < min) {
        min
    } else if (this > max) {
        max
    } else {
        this
    }
}

fun mix(v1: Float, weight1: Int, v2: Float, weight2: Int): Float {
    return (v1 * weight1 + v2 * weight2) / (abs(weight1) + abs(weight2))
}
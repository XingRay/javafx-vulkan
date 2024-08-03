package com.android.internal.util

class FastMath {
    companion object {
        @JvmStatic
        fun round(value: Float): Int {
            return Math.round(value)
        }
    }
}
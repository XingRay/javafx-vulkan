package com.github.xingray.javafx

import android.util.Log

private val TAG = "StyleExtension"

fun String.styleToMap(): MutableMap<String, String> {
    val map = mutableMapOf<String, String>()
    if (isBlank()) {
        return map
    }
    if (!contains(";")) {
        val keyAndValue = split(":")
        if (keyAndValue.size != 2) {
            Log.e(TAG, "styleToMap: error style: ${this}")
            throw IllegalArgumentException("styleToMap: error style: ${this}")
        }
        map[keyAndValue[0]] = keyAndValue[1]
        return map
    }
    val entries = split(";")
    if (entries.isEmpty()) {
        return map
    }

    entries.filter { it.isNotBlank() }.map {
        val keyAndValue = it.split(":")
        if (keyAndValue.size != 2) {
            Log.e(TAG, "styleToMap: error style entry: ${it}")
            throw IllegalArgumentException("styleToMap: error style entry: ${it}")
        }
        map.put(keyAndValue[0], keyAndValue[1].trim())
    }

    return map
}

fun Map<String, String>.mapToStyleString(): String {
    if (this.isEmpty()) {
        return ""
    }
    val stringBuilder = StringBuilder()
    this.forEach { (key, value) ->
        stringBuilder.append("${key}: ${value};")
    }
    return stringBuilder.toString()
}

fun String.setStyle(key: String, value: String): String {
    val style = this
    if (style.isEmpty()) {
        return "${key}: ${value};"
    }
    if (this.contains(key)) {
        val map = style.styleToMap()
        map.put(key, value)
        return map.mapToStyleString()
    } else {
        if (style.endsWith(";")) {
            return "${style}${key}: ${value};"
        } else {
            return "${style};${key}: ${value};"
        }
    }
}
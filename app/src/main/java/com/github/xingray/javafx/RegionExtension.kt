package com.github.xingray.javafx

import javafx.scene.layout.Region

private val TAG = "RegionExtension"


fun Region.setSize(value: Double) {
    setSize(value, value)
}

fun Region.setSize(width: Double, height: Double) {
    setWidth(width)
    setHeight(height)
}

fun Region.setWidth(width: Double) {
    prefWidth = width
    maxWidth = width
    minWidth = width
}

fun Region.setHeight(height: Double) {
    prefHeight = height
    maxHeight = height
    minHeight = height
}
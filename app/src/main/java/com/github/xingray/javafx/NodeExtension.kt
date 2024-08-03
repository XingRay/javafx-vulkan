package com.github.xingray.javafx

import javafx.scene.Node
import javafx.scene.paint.Color

private val TAG = "NodeExtension"

private const val FX_BACKGROUND_COLOR = "-fx-background-color"
private const val FX_WIDTH = "-fx-width"
private const val FX_HEIGHT = "-fx-height"

fun Node.setStyle(key: String, value: String) {
    style = style.setStyle(key, value)
}

fun Node.setBackgroundAsColor(color: Color) {
    setStyle(FX_BACKGROUND_COLOR, color.toHexString())
}

fun Node.setWidthPixel(width: Int) {
    setStyle(FX_WIDTH, "${width}px")
}

fun Node.setHeightPixel(height: Int) {
    setStyle(FX_HEIGHT, "${height}px")
}
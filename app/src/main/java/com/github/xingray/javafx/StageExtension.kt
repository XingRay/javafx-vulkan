package com.github.xingray.javafx

import javafx.stage.Stage

private val TAG = "StageExtension"

fun Stage.setSize(width: Double, height: Double) {
    setFixedWidth(width)
    setFixedHeight(height)
}

fun Stage.setFixedWidth(width: Double) {
    val stage = this
    stage.width = width
    stage.maxWidth = width
    stage.minWidth = width
}

fun Stage.setFixedHeight(height: Double) {
    val stage = this
    stage.height = height
    stage.maxHeight = height
    stage.minHeight = height
}
package com.github.xingray.javafx

import javafx.scene.Node
import javafx.scene.layout.AnchorPane

private val TAG = "AnchorPaneExtension"

fun Node.anchor(left: Double? = null, right: Double? = null, top: Double? = null, bottom: Double? = null) {
    AnchorPane.setLeftAnchor(this, left)
    AnchorPane.setRightAnchor(this, right)
    AnchorPane.setTopAnchor(this, top)
    AnchorPane.setBottomAnchor(this, bottom)
}

fun Node.anchor(value: Double? = null) {
    anchor(value, value, value, value)
}

fun Node.anchor(horizontal: Double? = null, vertical: Double? = null) {
    anchor(horizontal, horizontal, vertical, vertical)
}
package com.github.xingray.android.graphic


import com.github.xingray.android.resource.Resource
import javafx.scene.image.Image

private val TAG = "JavaFxImageExtension"

fun Resource.readImage(): Image? {
    return use { Image(it) }
}
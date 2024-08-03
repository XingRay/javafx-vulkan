package com.github.xingray.javafx

import com.github.xingray.android.resource.JarInnerResource
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.io.File
import java.io.InputStream

private val TAG = "ImageViewExtension"

fun ImageView.setSize(width: Double, height: Double) {
    fitWidth = width
    fitHeight = height
}

fun ImageView.setImageResource(resourcePath: String) {
    this.image = JarInnerResource(resourcePath).use { Image(it) }
}

fun ImageView.setImageFile(file: File) {
    setImageInputStream(file.inputStream())
}

fun ImageView.setImageInputStream(inputStream: InputStream) {
    this.image = inputStream.use { Image(it) }
}
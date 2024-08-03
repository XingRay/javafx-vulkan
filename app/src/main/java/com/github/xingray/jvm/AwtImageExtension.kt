package com.github.xingray.jvm

import android.util.Log
import com.github.xingray.android.resource.Resource
import com.github.xingray.image.ImageFormat
import com.github.xingray.image.InputImage
import java.awt.AlphaComposite
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


private val TAG = "AwtImageExtension"

fun InputImage.rgbaInputImageToBufferedImage(): BufferedImage? {
    val inputImage = this
    if (inputImage.format != ImageFormat.RGBA) {
        throw IllegalArgumentException("format not RGBA")
    }

    if (inputImage.width <= 0 || inputImage.height <= 0) {
        return null
    }

    // 检查输入数据是否为空
    val srcData = inputImage.data ?: let {
        return null
    }

    // TYPE_4BYTE_ABGR 高位到低位是 A B G R, 在 byte[] 中的排列就是 r g b a
    val bufferedImage = BufferedImage(inputImage.width, inputImage.height, BufferedImage.TYPE_4BYTE_ABGR)

    bufferedImage.raster.setDataElements(0, 0, inputImage.width, inputImage.height, srcData)
    return bufferedImage
}

fun List<Resource>.readAsBufferedImage(): BufferedImage? {
    if (isEmpty()) {
        return null
    }
    val firstImage = this[0].readAsBufferedImage() ?: let {
        Log.d(TAG, "readAsBufferedImage: firstImage is null")
        return null
    }
    if (size == 1) {
        return firstImage
    }
    return readAsBufferedImage(firstImage.width, firstImage.height)
}

fun List<Resource>.readAsBufferedImage(targetWidth: Int, targetHeight: Int): BufferedImage? {
    if (isEmpty()) {
        return null
    }
    if (size == 1) {
        val firstImage = this[0].readAsBufferedImage() ?: let {
            Log.d(TAG, "readAsBufferedImage: first image is null")
            return null
        }

        if (firstImage.width == targetWidth && firstImage.height == targetHeight) {
            return firstImage
        }
        return firstImage.createAsSize(targetWidth, targetHeight)
    }
    val targetImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB)
    val g2d = targetImage.createGraphics()

    forEach { resource ->
        val bufferedImage = resource.readAsBufferedImage() ?: let {
            Log.d(TAG, "readAsBufferedImage: bufferedImage is null")
            return@forEach
        }

        val originalWidth = bufferedImage.width
        val originalHeight = bufferedImage.height
        val originalImage = bufferedImage

        val scaleFactor: Double = Math.min(targetWidth.toDouble() / originalWidth, targetHeight.toDouble() / originalHeight)
        val scaledWidth = (originalWidth * scaleFactor).toInt()
        val scaledHeight = (originalHeight * scaleFactor).toInt()

        // 创建缩放后的图片
        val scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, java.awt.Image.SCALE_SMOOTH)


        // 设置透明度
        val opacity = 0.5f // 设置透明度为50%
        val alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity)
        g2d.composite = alphaComposite

        // 在目标图片上绘制缩放后的图片
        val x: Int = (targetWidth - scaledWidth) / 2
        val y: Int = (targetHeight - scaledHeight) / 2
        g2d.drawImage(scaledImage, x, y, null)
    }

    g2d.dispose()

    return targetImage
}

fun BufferedImage.createAsSize(targetWidth: Int, targetHeight: Int): BufferedImage {
    val originalWidth = this.width
    val originalHeight = this.height
    val originalImage = this

    // TODO:  可以优化,  宽/高 一边相等时可能 scaleFactor == 1 不需要缩放
    val scaleFactor: Double = Math.min(targetWidth.toDouble() / originalWidth, targetHeight.toDouble() / originalHeight)
    val scaledWidth = (originalWidth * scaleFactor).toInt()
    val scaledHeight = (originalHeight * scaleFactor).toInt()

    // 创建缩放后的图片
    val scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, java.awt.Image.SCALE_SMOOTH)
    val targetImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB)

    val g2d = targetImage.createGraphics()

    // 在目标图片上绘制缩放后的图片
    val x: Int = (targetWidth - scaledWidth) / 2
    val y: Int = (targetHeight - scaledHeight) / 2
    g2d.drawImage(scaledImage, x, y, null)
    g2d.dispose()

    return targetImage
}

fun Resource.readAsBufferedImage(): BufferedImage? {
    return use { ImageIO.read(it) }
}


fun List<Pair<Resource, Float>>.readAsBufferedImageWithAlpha(targetWidth: Int, targetHeight: Int): BufferedImage? {
    if (isEmpty()) {
        return null
    }
    if (size == 1) {
        val firstImage = this[0].readAsBufferedImage() ?: let {
            Log.d(TAG, "readAsBufferedImage: first image is null")
            return null
        }

        if (firstImage.width == targetWidth && firstImage.height == targetHeight) {
            return firstImage
        }
        return firstImage.createAsSize(targetWidth, targetHeight)
    }
    val targetImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB)
    val g2d = targetImage.createGraphics()

    forEach { pair ->
        val resource = pair.first
        val alpha = pair.second
        val bufferedImage = resource.readAsBufferedImage() ?: let {
            return@forEach
        }

        val originalWidth = bufferedImage.width
        val originalHeight = bufferedImage.height
        val originalImage = bufferedImage

        val scaleFactor: Double = Math.min(targetWidth.toDouble() / originalWidth, targetHeight.toDouble() / originalHeight)
        val scaledWidth = (originalWidth * scaleFactor).toInt()
        val scaledHeight = (originalHeight * scaleFactor).toInt()

        // 创建缩放后的图片
        val scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, java.awt.Image.SCALE_SMOOTH)

        // 设置透明度
        g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)

        // 在目标图片上绘制缩放后的图片
        val x: Int = (targetWidth - scaledWidth) / 2
        val y: Int = (targetHeight - scaledHeight) / 2
        g2d.drawImage(scaledImage, x, y, null)
    }

    g2d.dispose()

    return targetImage
}

private fun Pair<Resource, Float>.readAsBufferedImage(): BufferedImage? {
    val resource = this.first
    val alpha = this.second
    return resource.readAsBufferedImage(alpha)
}

fun Resource.readAsBufferedImage(alpha: Float): BufferedImage? {
    val originalImage = use { ImageIO.read(it) } ?: let {
        Log.d(TAG, "readAsBufferedImage: can not read image")
        return null
    }
    if (alpha == 1.0f) {
        return originalImage
    }

    // 创建一个新的BufferedImage对象，以便在其上设置透明度
    val transparentImage = BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB)
    val g2d = transparentImage.createGraphics()

    // 设置透明度
    val opacity = alpha
    val alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity)
    g2d.composite = alphaComposite

    // 绘制具有透明度的图片
    g2d.drawImage(originalImage, 0, 0, null)
    g2d.dispose()

    return transparentImage
}
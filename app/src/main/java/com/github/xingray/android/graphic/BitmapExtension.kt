package com.github.xingray.android.graphic

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log

private val TAG = "BitmapExtension"

//fun Bitmap.merge(bitmap: Bitmap): Bitmap {
//    val output = this.copy(Bitmap.Config.ARGB_8888, true)
//    val canvas = Canvas(output)
//    canvas.drawBitmap(bitmap, 0.0f, 0.0f, Paint())
//    return output
//}
//
//fun Bitmap.newEmpty(): Bitmap {
//    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//}
//
//fun Bitmap.setAllPixels(@ColorInt pixels: IntArray, width: Int, height: Int) {
//    this.setPixels(pixels, 0, width, 0, 0, width, height)
//}
//
//fun Bitmap.saveAs(saveFile: File) {
//    FileOutputStream(saveFile).use {
//        this.compress(Bitmap.CompressFormat.PNG, 100, it)
//        it.flush()
//    }
//}

//fun Bitmap.newClip(rect: Rect): Bitmap {
//    return newClip(rect.left, rect.top, rect.width(), rect.height())
//}

//fun Bitmap.newClip(x: Int, y: Int, width: Int, height: Int): Bitmap {
//    return Bitmap.createBitmap(this, x, y, width, height)
//}

//fun Bitmap.newScaleToFit(targetWidth: Int, targetHeight: Int): Bitmap {
//    val matrix = calcMatrixScaleToFit(targetWidth, targetHeight)
//    return newApplyMatrix(targetWidth, targetHeight, matrix)
//}

//fun Bitmap.newApplyMatrix(targetWidth: Int, targetHeight: Int, matrix: Matrix): Bitmap {
//    // 创建目标大小的 Bitmap，并将缩放后的 Bitmap 居中绘制在其中
//    val resultBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
//    val canvas = Canvas(resultBitmap)
//    canvas.drawBitmap(this, matrix, null)
//
//    return resultBitmap
//}

//fun Bitmap.calcMatrixScaleToFit(targetWidth: Int, targetHeight: Int): Matrix {
//    return calcMatrixScaleToFit(this.width, this.height, targetWidth, targetHeight)
//}

//fun calcMatrixScaleToFit(srcWidth: Int, srcHeight: Int, targetWidth: Int, targetHeight: Int): Matrix {
//    // 计算目标尺寸与原始尺寸的宽高比
//    val targetRatio = targetWidth.toFloat() / targetHeight.toFloat()
//    val srcRatio = srcWidth.toFloat() / srcHeight.toFloat()
//    Log.d(TAG, "Bitmap.newScaleToFit: targetRatio:${targetRatio}, srcRatio:${srcRatio}")
//
//    // 计算实际需要读取的图片尺寸
//    var actualWidth = targetWidth
//    var actualHeight = targetHeight
//    if (actualWidth * targetHeight != actualHeight * targetWidth) {
//        if (srcRatio > targetRatio) {
//            actualHeight = (targetWidth.toFloat() / srcRatio).toInt()
//        } else {
//            actualWidth = (targetHeight.toFloat() * srcRatio).toInt()
//        }
//    }
//    Log.d(TAG, "Bitmap.newScaleToFit: actualWidth:${actualWidth}, actualHeight:${actualHeight}")
//
//    val scale = calculateScaleMin(srcWidth, srcHeight, actualWidth, actualHeight)
//    Log.d(TAG, "readBitmap: scale:${scale}")
//
//    val matrix = Matrix()
//    matrix.postTranslate(-srcWidth * 0.5f, -srcHeight * 0.5f)
//    matrix.postScale(scale, scale)
//    matrix.postTranslate(targetWidth * 0.5f, targetHeight * 0.5f)
//
//    return matrix
//}

fun calculateInSampleSize(srcWidth: Int, srcHeight: Int, targetWidth: Int, targetHeight: Int): Int {
    var inSampleSize = 1
    if (srcHeight > targetHeight || srcWidth > targetWidth) {
        val heightRatio = (srcHeight.toFloat() / targetHeight).toInt()
        val widthRatio = (srcWidth.toFloat() / targetWidth).toInt()
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    return inSampleSize
}

fun calculateScaleMin(srcWidth: Int, srcHeight: Int, targetWidth: Int, targetHeight: Int): Float {
    Log.d(TAG, "calculateScaleMin: srcWidth:${srcWidth}, srcHeight:${srcHeight}, targetWidth:${targetWidth}, targetHeight:${targetHeight}")
    val heightRatio = targetHeight.toFloat() / srcHeight.toFloat()
    val widthRatio = targetWidth.toFloat() / srcWidth.toFloat()
    Log.d(TAG, "calculateScaleMin: heightRatio:${heightRatio}, widthRatio:${widthRatio}")
    return Math.min(heightRatio, widthRatio)
}

fun calculateScaleMax(srcWidth: Int, srcHeight: Int, targetWidth: Int, targetHeight: Int): Float {
    Log.d(TAG, "calculateScaleMax: srcWidth:${srcWidth}, srcHeight:${srcHeight}, targetWidth:${targetWidth}, targetHeight:${targetHeight}")
    val heightRatio = targetHeight.toFloat() / srcHeight.toFloat()
    val widthRatio = targetWidth.toFloat() / srcWidth.toFloat()
    Log.d(TAG, "calculateScaleMax: heightRatio:${heightRatio}, widthRatio:${widthRatio}")
    return Math.max(heightRatio, widthRatio)
}

//fun Bitmap.toPngByteArray(): ByteArray {
//    ByteArrayOutputStream().use { byteArrayOutputStream ->
//        this.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
//        return byteArrayOutputStream.toByteArray()
//    }
//}

// 裁剪 Bitmap，仅保留不透明的部分，并记录裁剪部分在原始图片中的位置及大小
//fun Bitmap.crop(): Triple<Bitmap, Int, Int> {
//    val bitmap = this
//    val width = bitmap.width
//    val height = bitmap.height
//
//    var top = -1
//    var bottom = -1
//    var left = -1
//    var right = -1
//
//    // 找到不透明区域的边界
//    for (y in 0 until height) {
//        for (x in 0 until width) {
//            val alpha = bitmap.getPixel(x, y).alpha
//            if (alpha != 0) { // 如果像素不透明
//                if (left == -1 || x < left) {
//                    left = x
//                }
//                if (top == -1 || y < top) {
//                    top = y
//                }
//                if (right == -1 || x > right) {
//                    right = x
//                }
//                if (bottom == -1 || y > bottom) {
//                    bottom = y
//                }
//            }
//        }
//    }
//
//    // 计算裁剪后的矩形宽度和高度
//    val cropWidth = right - left + 1
//    val cropHeight = bottom - top + 1
//
//    // 创建一个新的 Bitmap 作为裁剪结果
//    val croppedBitmap = Bitmap.createBitmap(cropWidth, cropHeight, Bitmap.Config.ARGB_8888)
//
//    // 创建一个 Canvas，并在其上绘制裁剪结果
//    val canvas = Canvas(croppedBitmap)
//    val srcRect = Rect(left, top, right + 1, bottom + 1) // 注意边界的处理
//    val destRect = Rect(0, 0, cropWidth, cropHeight)
//    canvas.drawBitmap(bitmap, srcRect, destRect, null)
//
//    return Triple(croppedBitmap, left, top)
//}
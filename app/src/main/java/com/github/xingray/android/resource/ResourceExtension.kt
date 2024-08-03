package com.github.xingray.android.resource

//import android.graphics.BitmapFactory
//import android.util.Size
//import androidx.exifinterface.media.ExifInterface
//import com.github.xingray.android.readBitmap
//import com.github.xingray.android.readImageSize
//import com.github.xingray.android.rotate
import android.graphics.Bitmap
import android.util.Log
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import javax.imageio.ImageIO


private val TAG = "ResourceExtension"

fun Resource.readBitmap(): Bitmap? {
//    return use {
//        it.readBitmap()
//    }
    return null
}

fun Resource.readBitmapAsSize(): BufferedImage? {
    return use { ImageIO.read(it) }
}

fun Resource.readBitmapAsSize(width: Int, height: Int): BufferedImage? {
    Log.d(TAG, "InputStreamProvider.readBitmap: width:${width}, height:${height}")


//    val bitmap = io.github.humbleui.skija.Bitmap()
//    val canvas =  io.github.humbleui.skija.Canvas(bitmap)

    return use { ImageIO.read(it) }

//    val exifInterface = this.use { inputStream ->
//        ExifInterface(inputStream)
//    } ?: let {
//        Log.e(TAG, "readBitmap: can not open inputStream, InputStreamProvider:${this}")
//        return null
//    }
//
//    val rotation = exifInterface.rotationDegrees
//    val orientation: Int = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
//    val mirror = orientation == ExifInterface.ORIENTATION_TRANSVERSE || orientation == ExifInterface.ORIENTATION_FLIP_VERTICAL
//            || orientation == ExifInterface.ORIENTATION_TRANSPOSE || orientation == ExifInterface.ORIENTATION_FLIP_HORIZONTAL
//    val exifWidth = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0)
//    val exifHeight = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0)
//    Log.d(TAG, "InputStream.readBitmap: exifInterface: rotation:${rotation}, mirror:${mirror}, exifWidth:${exifWidth}, exifHeight:${exifHeight}")
//
//    val size = if (exifWidth <= 0 || exifHeight <= 0) {
//        this.use { inputStream -> inputStream.readImageSize() } ?: let {
//            Log.e(TAG, "readBitmap: can not open inputStream, InputStreamProvider:${this}")
//            return null
//        }
//    } else {
//        Size(exifWidth, exifHeight)
//    }
//
//    val rotateSize = size.rotate(rotation)
//    val srcWidth = rotateSize.width
//    val srcHeight = rotateSize.height
//    Log.d(TAG, "readBitmap: srcWidth:${srcWidth}, srcHeight:${srcHeight}")
//    if (srcWidth == width && srcHeight == height) {
//        return this.readBitmap()
//    }
//    // 计算目标尺寸与原始尺寸的宽高比
//    val targetRatio = width.toFloat() / height.toFloat()
//    val srcRatio = srcWidth.toFloat() / srcHeight.toFloat()
//    Log.d(TAG, "readBitmap: targetRatio:${targetRatio}, srcRatio:${srcRatio}")
//
//    // 计算实际需要读取的图片尺寸
//    var actualWidth = width
//    var actualHeight = height
//    if (targetRatio != srcRatio) {
//        if (srcRatio > targetRatio) {
//            actualHeight = (width.toFloat() / srcRatio).toInt()
//        } else {
//            actualWidth = (height.toFloat() * srcRatio).toInt()
//        }
//    }
//    Log.d(TAG, "readBitmap: actualWidth:${actualWidth}, actualHeight:${actualHeight}")
//
//    // 重新读取图片并缩放
//    val options = BitmapFactory.Options()
//    val sampleSize = calculateInSampleSize(srcWidth, srcHeight, actualWidth, actualHeight)
//    options.inSampleSize = sampleSize
//    Log.d(TAG, "readBitmap: options.inSampleSize:${options.inSampleSize}")
//
//    val scale = calculateScaleMin(srcWidth, srcHeight, actualWidth, actualHeight) * sampleSize
//    Log.d(TAG, "readBitmap: scale:${scale}")
//
//    val readBitmap = this.use { inputStream -> BitmapFactory.decodeStream(inputStream, null, options) } ?: let {
//        Log.e(TAG, "readBitmap: can not open inputStream, InputStreamProvider:${this}")
//        return null
//    }
//    Log.d(TAG, "readBitmap: readBitmap, width:${readBitmap.width}, height:${readBitmap.height}")
//
//    val matrix = Matrix()
//    matrix.postTranslate(-size.width * 0.5f / sampleSize, -size.height * 0.5f / sampleSize)
//    matrix.postRotate(rotation.toFloat())
//    if (mirror) {
//        matrix.postScale(-scale, scale)
//    } else {
//        matrix.postScale(scale, scale)
//    }
//    matrix.postTranslate(width * 0.5f, height * 0.5f)
//
//    // 创建目标大小的 Bitmap，并将缩放后的 Bitmap 居中绘制在其中
//    val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//    val canvas = Canvas(resultBitmap)
//    canvas.drawBitmap(readBitmap, matrix, null)
//
//    return resultBitmap
}


//fun Resource.readBitmapSize(): Size? {
//    val exifInterface = this.use { inputStream ->
//        ExifInterface(inputStream)
//    } ?: let {
//        Log.e(TAG, "readBitmap: can not open inputStream, InputStreamProvider:${this}")
//        return null
//    }
//
//    val rotation = exifInterface.rotationDegrees
//    val orientation: Int = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
//    val mirror = orientation == ExifInterface.ORIENTATION_TRANSVERSE || orientation == ExifInterface.ORIENTATION_FLIP_VERTICAL
//            || orientation == ExifInterface.ORIENTATION_TRANSPOSE || orientation == ExifInterface.ORIENTATION_FLIP_HORIZONTAL
//    val exifWidth = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0)
//    val exifHeight = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0)
//    Log.d(TAG, "InputStream.readBitmap: exifInterface: rotation:${rotation}, mirror:${mirror}, exifWidth:${exifWidth}, exifHeight:${exifHeight}")
//
//    val size = if (exifWidth <= 0 || exifHeight <= 0) {
//        this.use { inputStream -> inputStream.readImageSize() } ?: let {
//            Log.e(TAG, "readBitmap: can not open inputStream, InputStreamProvider:${this}")
//            return null
//        }
//    } else {
//        Size(exifWidth, exifHeight)
//    }
//
//    return size.rotate(rotation)
//}
//
//fun Size.rotate(degree: Int): Size {
//    return if (degree == 90 || degree == 270) {
//        Size(height, width)
//    } else {
//        this
//    }
//}

fun Resource.copyTo(file: File) {
    if (this is FileResource && this.file.absolutePath == file.absolutePath) {
        return
    }

    use { inputStream ->
        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
}

fun File.resoleAsFileResource(fileName: String?): FileResource? {
    if (fileName.isNullOrEmpty()) {
        return null
    }
    val dir = this
    if (!dir.exists() || dir.isFile) {
        return null
    }

    val file = dir.resolve(fileName)
    if (!file.exists() || !file.isFile) {
        return null
    }
    return FileResource(file)
}
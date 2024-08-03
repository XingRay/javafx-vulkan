package com.github.xingray.image

import android.graphics.Bitmap
import android.util.Log
import java.awt.image.*


private val TAG = object : Any() {}::class.java.enclosingClass.simpleName ?: ""
fun InputImage.toBitmap(): Bitmap {
    val width = this.width
    val height = this.height
    val data = this.data

//    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//    val pixels = IntArray(width * height)
//
//    ImageUtil.nv21ToArgb(data, width, height, pixels)
//    bitmap.setAllPixels(pixels, width, height)
//
//    return bitmap
    return Bitmap()
}

fun BufferedImage.toRgbaInputImage(): InputImage? {
    val width = this.width
    val height = this.height
    Log.d(TAG, "toInputImage: width:${width}, height:${height}")

    Log.d(TAG, "toInputImage: colorModel:${this.colorModel.javaClass.simpleName}")
    Log.d(TAG, "toInputImage: colorModel:${this.colorModel}")

    if (colorModel is DirectColorModel) {
        val raster = this.raster
        val dataBuffer = raster.dataBuffer

        if (dataBuffer.dataType == DataBufferByte.TYPE_BYTE) {
            val pixelData = (dataBuffer as DataBufferByte).data
            return InputImage(width, height, pixelData)
        } else if (dataBuffer.dataType == DataBufferByte.TYPE_INT) {
            val pixelData = ByteArray(width * height * 4)
            val intPixelData = (dataBuffer as DataBufferInt).data
            var pixelIndex = 0
            for (pixel in intPixelData) {
                pixelData[pixelIndex++] = (pixel shr 16 and 0xFF).toByte() // 获取红色分量
                pixelData[pixelIndex++] = (pixel shr 8 and 0xFF).toByte()  // 获取绿色分量
                pixelData[pixelIndex++] = (pixel and 0xFF).toByte()         // 获取蓝色分量
                pixelData[pixelIndex++] = (pixel shr 24 and 0xFF).toByte() // 获取Alpha分量
            }
            return InputImage(width, height, pixelData)
        } else {
            Log.e(TAG, "Unsupported data type")
            throw UnsupportedOperationException("not supported yet, dataBuffer.dataType:${dataBuffer.dataType}")
        }
    } else if (colorModel is ComponentColorModel) {
        if (colorModel.numComponents == 3) {
            // RGB 颜色模型，通常是 3 字节表示一个像素
            val dataBuffer = this.raster.dataBuffer ?: let {
                Log.d(TAG, "toInputImage: dataBuffer is null")
                return null
            }
            val dataType = dataBuffer.dataType
            Log.d(TAG, "toInputImage: dataType:${dataType}")
            if (dataType == DataBuffer.TYPE_BYTE) {
                val data = (dataBuffer as DataBufferByte).data
                Log.d(TAG, "toInputImage: data.size:${data.size}")
                return InputImage(width, height, data, format = ImageFormat.BGR).bgrToRgba()
            } else {
                throw UnsupportedOperationException("todo")
            }
        } else if (colorModel.numComponents == 4) {
            // ARGB 颜色模型，通常是 4 字节表示一个像素，第一个字节是 alpha 通道
            val dataBuffer = this.raster.dataBuffer ?: let {
                Log.d(TAG, "toInputImage: dataBuffer is null")
                return null
            }
            val dataType = dataBuffer.dataType
            Log.d(TAG, "toInputImage: dataType:${dataType}")
            if (dataType == DataBuffer.TYPE_BYTE) {
                val data = (dataBuffer as DataBufferByte).data
                Log.d(TAG, "toInputImage: data.size:${data.size}")
                return InputImage(width, height, data, format = ImageFormat.ABGR).abgrToRgba()
            } else {
                throw UnsupportedOperationException("todo")
            }
        } else {
            throw UnsupportedOperationException("todo")
        }
    } else {
        // 其他类型，根据实际情况处理
        throw UnsupportedOperationException("todo")
    }
}

fun InputImage.setPixel(red: Int, green: Int, blue: Int, alpha: Int): InputImage {
    return setPixel(red.toByte(), green.toByte(), blue.toByte(), alpha.toByte())
}

fun InputImage.setPixel(red: Byte, green: Byte, blue: Byte, alpha: Byte): InputImage {
    val data = this.data ?: return this

    when (this.format) {
        ImageFormat.RGB -> {
            for (row in 0 until height) {
                for (column in 0 until width) {
                    data[(row * width + column) * 3 + 0] = red
                    data[(row * width + column) * 3 + 1] = green
                    data[(row * width + column) * 3 + 2] = blue
                }
            }
        }

        ImageFormat.RGBA -> {
            for (row in 0 until height) {
                for (column in 0 until width) {
                    data[(row * width + column) * 4 + 0] = red
                    data[(row * width + column) * 4 + 1] = green
                    data[(row * width + column) * 4 + 2] = blue
                    data[(row * width + column) * 4 + 3] = alpha
                }
            }
        }

        ImageFormat.ARGB -> {
            for (row in 0 until height) {
                for (column in 0 until width) {
                    data[(row * width + column) * 4 + 0] = alpha
                    data[(row * width + column) * 4 + 1] = red
                    data[(row * width + column) * 4 + 2] = green
                    data[(row * width + column) * 4 + 3] = blue
                }
            }
        }


        ImageFormat.BGR -> {
            for (row in 0 until height) {
                for (column in 0 until width) {
                    data[(row * width + column) * 3 + 0] = blue
                    data[(row * width + column) * 3 + 1] = green
                    data[(row * width + column) * 3 + 2] = red
                }
            }
        }

        ImageFormat.BGRA -> {
            for (row in 0 until height) {
                for (column in 0 until width) {
                    data[(row * width + column) * 4 + 0] = blue
                    data[(row * width + column) * 4 + 1] = green
                    data[(row * width + column) * 4 + 2] = red
                    data[(row * width + column) * 4 + 3] = alpha
                }
            }
        }

        ImageFormat.ABGR -> {
            for (row in 0 until height) {
                for (column in 0 until width) {
                    data[(row * width + column) * 4 + 0] = alpha
                    data[(row * width + column) * 4 + 1] = blue
                    data[(row * width + column) * 4 + 2] = green
                    data[(row * width + column) * 4 + 3] = red
                }
            }
        }

        else -> {
            throw IllegalArgumentException("this.format:${this.format}")
        }
    }
    return this
}

//fun BufferedImage.toInputImage(rotate: Int = 0, mirror: Boolean = false): InputImage {
//    val width = this.getWidth()
//    val height = this.getHeight()
//    //保存所有的像素的数组，图片宽×高
//    val pixels = this.raster.dataBuffer.
//
//    val bytes = ByteArray(width * height * 4)
//    for (i in pixels.indices) {
//        val argb = pixels[i].toLong()
//        val alpha: Byte = (argb and 0xff000000 shr 24).toByte() //取高两位
//        val red = (argb and 0x00ff0000 shr 16).toByte() //取高两位
//        val green = (argb and 0x0000ff00 shr 8).toByte() //取中两位
//        val blue = (argb and 0x000000ff).toByte() //取低两位
//        bytes[i * 4 + 0] = red
//        bytes[i * 4 + 1] = green
//        bytes[i * 4 + 2] = blue
//        bytes[i * 4 + 3] = alpha
//    }
//
//    return InputImage(width, height, bytes, rotate, mirror, ImageFormat.RGBA)
//}

fun InputImage.bgrToRgba(): InputImage {
    if (this.format != ImageFormat.BGR) {
        throw IllegalArgumentException("this.format:${this.format}")
    }

    val bgr = this.data ?: let {
        throw IllegalArgumentException("this.data is null")
    }

    val rgba = ByteArray(this.width * this.height * 4)
    for (row in 0 until height) {
        for (column in 0 until width) {
            val blue = bgr[(row * width + column) * 3 + 0]
            val green = bgr[(row * width + column) * 3 + 1]
            val red = bgr[(row * width + column) * 3 + 2]
            val alpha = 255.toByte()

            rgba[(row * width + column) * 4 + 0] = red
            rgba[(row * width + column) * 4 + 1] = green
            rgba[(row * width + column) * 4 + 2] = blue
            rgba[(row * width + column) * 4 + 3] = alpha
        }
    }

    return InputImage(width, height, rgba, rotate, mirror, ImageFormat.RGBA)
}

fun InputImage.rgbToRgba(): InputImage {
    if (this.format != ImageFormat.RGB) {
        throw IllegalArgumentException("this.format:${this.format}")
    }

    val rgb = this.data ?: let {
        throw IllegalArgumentException("this.data is null")
    }

    val rgba = ByteArray(this.width * this.height * 4)
    for (row in 0 until height) {
        for (column in 0 until width) {
            val red = rgb[(row * width + column) * 3 + 0]
            val green = rgb[(row * width + column) * 3 + 1]
            val blue = rgb[(row * width + column) * 3 + 2]
            val alpha = 255.toByte()

            rgba[(row * width + column) * 4 + 0] = red
            rgba[(row * width + column) * 4 + 1] = green
            rgba[(row * width + column) * 4 + 2] = blue
            rgba[(row * width + column) * 4 + 3] = alpha
        }
    }

    return InputImage(width, height, rgba, rotate, mirror, ImageFormat.RGBA)
}


fun InputImage.rgbaToArgb(): InputImage {
    if (this.format != ImageFormat.RGBA) {
        throw IllegalArgumentException("this.format:${this.format}")
    }

    val rgba = this.data ?: let {
        throw IllegalArgumentException("this.data is null")
    }

    val argb = ByteArray(this.width * this.height * 4)
    for (row in 0 until height) {
        for (column in 0 until width) {
            val red = rgba[(row * width + column) * 4 + 0]
            val green = rgba[(row * width + column) * 4 + 1]
            val blue = rgba[(row * width + column) * 4 + 2]
            val alpha = rgba[(row * width + column) * 4 + 3]

            argb[(row * width + column) * 4 + 0] = alpha
            argb[(row * width + column) * 4 + 1] = red
            argb[(row * width + column) * 4 + 2] = green
            argb[(row * width + column) * 4 + 3] = blue
        }
    }

    return InputImage(width, height, argb, rotate, mirror, ImageFormat.ARGB)
}

fun InputImage.argbToRgba(): InputImage {
    if (this.format != ImageFormat.ARGB) {
        throw IllegalArgumentException("this.format:${this.format}")
    }

    val argb = this.data ?: let {
        throw IllegalArgumentException("this.data is null")
    }

    val rgba = ByteArray(this.width * this.height * 4)
    for (row in 0 until height) {
        for (column in 0 until width) {
            val alpha = argb[(row * width + column) * 4 + 0]
            val red = argb[(row * width + column) * 4 + 1]
            val green = argb[(row * width + column) * 4 + 2]
            val blue = argb[(row * width + column) * 4 + 3]

            rgba[(row * width + column) * 4 + 0] = red
            rgba[(row * width + column) * 4 + 1] = green
            rgba[(row * width + column) * 4 + 2] = blue
            rgba[(row * width + column) * 4 + 3] = alpha
        }
    }

    return InputImage(width, height, rgba, rotate, mirror, ImageFormat.RGBA)
}


fun InputImage.rgbaToAbgr(): InputImage {
    if (this.format != ImageFormat.RGBA) {
        throw IllegalArgumentException("this.format:${this.format}")
    }

    val rgba = this.data ?: let {
        throw IllegalArgumentException("this.data is null")
    }

    val abgr = ByteArray(this.width * this.height * 4)
    for (row in 0 until height) {
        for (column in 0 until width) {
            val red = rgba[(row * width + column) * 4 + 0]
            val green = rgba[(row * width + column) * 4 + 1]
            val blue = rgba[(row * width + column) * 4 + 2]
            val alpha = rgba[(row * width + column) * 4 + 3]

            abgr[(row * width + column) * 4 + 0] = alpha
            abgr[(row * width + column) * 4 + 1] = blue
            abgr[(row * width + column) * 4 + 2] = green
            abgr[(row * width + column) * 4 + 3] = red
        }
    }

    return InputImage(width, height, abgr, rotate, mirror, ImageFormat.ABGR)
}


fun InputImage.abgrToRgba(): InputImage {
    if (this.format != ImageFormat.ABGR) {
        throw IllegalArgumentException("this.format:${this.format}")
    }

    val abgr = this.data ?: let {
        throw IllegalArgumentException("this.data is null")
    }

    val rgba = ByteArray(this.width * this.height * 4)
    for (row in 0 until height) {
        for (column in 0 until width) {
            val alpha = abgr[(row * width + column) * 4 + 0]
            val blue = abgr[(row * width + column) * 4 + 1]
            val green = abgr[(row * width + column) * 4 + 2]
            val red = abgr[(row * width + column) * 4 + 3]

            rgba[(row * width + column) * 4 + 0] = red
            rgba[(row * width + column) * 4 + 1] = green
            rgba[(row * width + column) * 4 + 2] = blue
            rgba[(row * width + column) * 4 + 3] = alpha
        }
    }

    return InputImage(width, height, rgba, rotate, mirror, ImageFormat.RGBA)
}

fun InputImage.bgraToRgba(): InputImage {
    if (this.format != ImageFormat.BGRA) {
        throw IllegalArgumentException("this.format:${this.format}")
    }

    val bgra = this.data ?: let {
        throw IllegalArgumentException("this.data is null")
    }

    val rgba = ByteArray(this.width * this.height * 4)
    for (row in 0 until height) {
        for (column in 0 until width) {
            val blue = bgra[(row * width + column) * 4 + 0]
            val green = bgra[(row * width + column) * 4 + 1]
            val red = bgra[(row * width + column) * 4 + 2]
            val alpha = bgra[(row * width + column) * 4 + 3]

            rgba[(row * width + column) * 4 + 0] = red
            rgba[(row * width + column) * 4 + 1] = green
            rgba[(row * width + column) * 4 + 2] = blue
            rgba[(row * width + column) * 4 + 3] = alpha
        }
    }

    return InputImage(width, height, rgba, rotate, mirror, ImageFormat.RGBA)
}

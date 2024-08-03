package com.github.xingray.image

data class InputImage(
    var width: Int = 0,
    var height: Int = 0,
    var data: ByteArray? = null,



/*
    \rotate   0        90           180         270
    mirror
            888888  88                 88   8888888888
      0     88      88  88             88       88  88
            8888    8888888888       8888           88
            88                         88
            88                     888888

            888888  8888888888     88              88
                88  88  88         88          88  88
      1       8888  88             8888    8888888888
                88                 88
                88                 888888

*/
    var rotate: Int = 0,
    var mirror: Boolean = false,

    var format: ImageFormat = ImageFormat.NV21
)
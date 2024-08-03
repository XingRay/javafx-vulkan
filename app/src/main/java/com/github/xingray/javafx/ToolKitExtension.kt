package com.github.xingray.javafx

class Toolkit {
    companion object {
        fun getScreenDensity(): Float {
            val toolkit = java.awt.Toolkit.getDefaultToolkit()
            val screenResolution = toolkit.screenResolution
            return screenResolution.toFloat() / 96.0f // 默认96 dpi为标准
        }
    }
}

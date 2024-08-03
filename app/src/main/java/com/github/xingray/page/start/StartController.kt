package com.github.xingray.page.start

import android.util.Log
import com.github.xingray.base.BaseController
import com.github.xingray.renderer.VulkanRenderer
import com.xingray.javafx.base.page.LayoutPath
import com.xingray.javafx.base.page.RoutePath
import javafx.fxml.FXML
import javafx.geometry.Rectangle2D
import javafx.scene.image.ImageView
import javafx.scene.image.PixelBuffer
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Region
import javafx.stage.Screen

@LayoutPath("/fxml/start.fxml")
@RoutePath("/start")
class StartController : BaseController() {

    companion object {
        @JvmStatic
        private val TAG = StartController::class.java.simpleName
    }

    lateinit var imageRegion: Region
    lateinit var rootView: AnchorPane

    @FXML
    private lateinit var ivImage: ImageView

    private lateinit var mVulkanRenderer: VulkanRenderer

    // Define the width and height of the image
    var mWidth = 0
    var mHeight = 0

    private var mDestroyed = false

    override fun onCreated() {
        val bounds = Screen.getPrimary().bounds
        mWidth = bounds.width.toInt()
        mHeight = bounds.height.toInt()
        Log.d(TAG, "onCreated: mWidth:${mWidth}, mHeight:${mHeight}")


        mVulkanRenderer = VulkanRenderer()
        val vulkanBuffer = mVulkanRenderer.createMappingByteBuffer()
        val pixelBuffer = PixelBuffer(mWidth, mHeight, vulkanBuffer, PixelFormat.getByteBgraPreInstance())
        val image = WritableImage(pixelBuffer)
        ivImage.image = image
        val region = Rectangle2D(0.0, 0.0, mWidth.toDouble(), mHeight.toDouble())

        ivImage.fitWidthProperty().bind(imageRegion.widthProperty())
        ivImage.fitHeightProperty().bind(imageRegion.heightProperty())

        ivImage.fitWidthProperty().addListener { _, oldValue, newValue ->
            Log.d(TAG, "ivImage.fitWidthProperty: newValue:${newValue}")
        }

        ivImage.viewportProperty().addListener { _, oldValue, newValue ->
            Log.d(TAG, "ivImage.viewportProperty: newValue:${newValue}")
        }

        rootView.widthProperty().addListener { _, oldValue, newValue ->
            Log.d(TAG, "rootView.widthProperty: newValue:${newValue}")
        }


        Thread {
            while (!mDestroyed) {
                mVulkanRenderer.drawFrame()
                mVulkanRenderer.copyImageToBuffer()
//                ivImage.image = WritableImage(pixelBuffer)

                runOnUiThread {
                    pixelBuffer.updateBuffer { region }
                }
                Thread.sleep(10)
            }

            mVulkanRenderer.release()
        }.start()
    }

    override fun onDestroy() {
        mDestroyed = true
    }
}
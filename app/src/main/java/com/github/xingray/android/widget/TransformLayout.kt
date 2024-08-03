package com.github.xingray.android.widget

import android.util.Log
import android.view.View
import com.github.xingray.android.graphic.readImage
import com.github.xingray.android.resource.JarInnerResource
import com.github.xingray.javafx.anchor
import com.github.xingray.javafx.setSize
import javafx.event.EventHandler
import javafx.geometry.Bounds
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane


class TransformLayout() : AnchorPane() {

    companion object {
        @JvmStatic
        private val TAG = TransformLayout::class.java.simpleName

        /**
         *  锚点索引
         *
         *  0  1  2
         *  3  4  5
         *  6  7  8
         *
         */
        private val anchorOffsets = arrayOf(
            arrayOf(0.0f, 0.0f), arrayOf(0.5f, 0.0f), arrayOf(1.0f, 0.0f),
            arrayOf(0.0f, 0.5f), arrayOf(0.5f, 0.5f), arrayOf(1.0f, 0.5f),
            arrayOf(0.0f, 1.0f), arrayOf(0.5f, 1.0f), arrayOf(1.0f, 1.0f),
        )
    }

    val mTargetView = ImageView()

    val mScaleIcon = ImageView()
    val mRotateIcon = ImageView()
    val mCloseIcon = ImageView()
    val mTranslateIcon = ImageView()

    private val mOperators = listOf(mScaleIcon, mRotateIcon, mCloseIcon, mTranslateIcon)

    private var mContainerWidth = 0.0
    private var mContainerHeight = 0.0

    var mWidthRatio: Double = 1.0
    var mHeightRatio: Double = 1.0

    private var mAnchorOffsetX: Double = 0.0
    private var mAnchorOffsetY: Double = 0.0

    private var mParentOffsetX: Double = 0.0
    private var mParentOffsetY: Double = 0.0

    private var mOnMoveListener: ((deltaX: Float, deltaY: Float) -> Unit)? = null
    fun setOnMoveListener(listener: (deltaX: Float, deltaY: Float) -> Unit) {
        mOnMoveListener = listener
    }

    private var mTranslateLastDragX: Double = 0.0
    private var mTranslateLastDragY: Double = 0.0

    private var mTranslateX: Double = 0.0
    private var mTranslateY: Double = 0.0

    private var mScaleLastDragX: Double = 0.0
    private var mScaleLastDragY: Double = 0.0

    private var mTargetScaleX: Double = 0.0
    private var mTargetScaleY: Double = 0.0

    private var mRotateOffset: Double = 0.0

    var mOperatorIconWidth = 40.0
    var mOperatorIconHeight = 40.0

    private var mShowOperatorIcon = true
    var showOperatorIcon
        get() = mShowOperatorIcon
        set(value) {
            if (mShowOperatorIcon == value) {
                return
            }
            mShowOperatorIcon = value

            mScaleIcon.isVisible = value
            mRotateIcon.isVisible = value
            mCloseIcon.isVisible = value
            mTranslateIcon.isVisible = value
        }


    init {
        // 添加缩放图标
        mScaleIcon.image = JarInnerResource("image/icon_expand.png").readImage()

        // 添加旋转图标
        mRotateIcon.image = JarInnerResource("image/icon_rotate.png").readImage()

        // 添加旋转图标
        mCloseIcon.image = JarInnerResource("image/icon_close.png").readImage()

        // 添加旋转图标
        mTranslateIcon.image = JarInnerResource("image/icon_translate.png").readImage()

        mCloseIcon.anchor(left = 0.0, top = 0.0)
        mRotateIcon.anchor(right = 0.0, top = 0.0)
        mTranslateIcon.anchor(left = 0.0, bottom = 0.0)
        mScaleIcon.anchor(right = 0.0, bottom = 0.0)

        children.addAll(mTargetView, mScaleIcon, mRotateIcon, mCloseIcon, mTranslateIcon)

        updateSize()
        updateTargetImageViewLayout()

        // javafx的鼠标事件对于带有透明的图片在部分区域无效
        // https://www.cnblogs.com/chenxinyuan/p/12096457.html
        mTargetView.isPickOnBounds = true
        mOperators.forEach { it.isPickOnBounds = true }


        mTranslateIcon.onMousePressed = EventHandler { e ->
            val x = e.sceneX
            val y = e.sceneY
            onDragStart(x, y)
        }

        mTranslateIcon.onMouseDragged = EventHandler { e ->
            val x = e.sceneX
            val y = e.sceneY
            onDragging(x, y)
        }

        mTargetView.onMousePressed = EventHandler { e ->
            val x = e.sceneX
            val y = e.sceneY
            onDragStart(x, y)
        }

        mTargetView.onMouseDragged = EventHandler { e ->
            val x = e.sceneX
            val y = e.sceneY
            onDragging(x, y)
        }


        // 拖拽调整控件大小
        mScaleIcon.onMousePressed = EventHandler {
            val x = it.sceneX
            val y = it.sceneY
            onScaleDragStart(x, y)
        }


        mScaleIcon.onMouseDragged = EventHandler {
            val x = it.sceneX
            val y = it.sceneY
            onScale(x, y)
        }

        // 拖拽调整控件大小
        mRotateIcon.onMousePressed = EventHandler {
            val x = it.sceneX
            val y = it.sceneY
            onRotateStart(x, y)
        }


        mRotateIcon.onMouseDragged = EventHandler {
            val x = it.sceneX
            val y = it.sceneY
            onRotate(x, y)
        }
    }

    private fun onDragStart(x: Double, y: Double) {
        mTranslateLastDragX = x
        mTranslateLastDragY = y
    }

    fun onDragging(x: Double, y: Double) {
        val parentWidth = mContainerWidth
        val parentHeight = mContainerHeight

        Log.d(TAG, "onDragging: x:$x, y:$y, mLastDragX:${mTranslateLastDragX}, mLastDragY:${mTranslateLastDragY}")
        val deltaX = x - mTranslateLastDragX
        val deltaY = y - mTranslateLastDragY
        Log.d(TAG, "onDragging: deltaX:$deltaX, deltaY:$deltaY, parentWidth:$parentWidth, parentHeight:$parentHeight")
        mTranslateX += deltaX / parentWidth
        mTranslateY += deltaY / parentHeight
        Log.d(TAG, "onDragging: mTranslateX:$mTranslateX, mTranslateY:$mTranslateY")

        mTranslateLastDragX = x
        mTranslateLastDragY = y

        updateLayoutPosition()
    }

    private fun onScaleDragStart(x: Double, y: Double) {
        mScaleLastDragX = x
        mScaleLastDragY = y
    }

    private fun onScale(x: Double, y: Double) {
        val parentWidth = mContainerWidth
        val parentHeight = mContainerHeight

        Log.d(TAG, "onScaleDragging: x:$x, y:$y, mScaleLastDragX:${mScaleLastDragX}, mScaleLastDragY:${mScaleLastDragY}")
        val deltaX = x - mScaleLastDragX
        val deltaY = y - mScaleLastDragY
        Log.d(TAG, "onScaleDragging: deltaX:$deltaX, deltaY:$deltaY, parentWidth:$parentWidth, parentHeight:$parentHeight")
        mTargetScaleX += deltaX / parentWidth
        mTargetScaleY += deltaY / parentHeight
        Log.d(TAG, "onScaleDragging: mTargetScaleX:$mTargetScaleX, mTargetScaleY:$mTargetScaleY")

        mScaleLastDragX = x
        mScaleLastDragY = y

        updateTargetImageViewSize()
        updateSize()
        updateRotateOffset()
        requestLayout()
    }


    private fun onRotateStart(x: Double, y: Double) {

    }

    private fun onRotate(x: Double, y: Double) {
        val boundsInScene: Bounds = mTargetView.localToScene(mTargetView.getBoundsInLocal())
        val centerX = (boundsInScene.minX + boundsInScene.maxX) / 2
        val centerY = (boundsInScene.minY + boundsInScene.maxY) / 2
        Log.d(TAG, "onRotate: centerX:$centerX, centerY:$centerY")
        this.rotate = Math.atan2(y - centerY, x - centerX) * 180 / Math.PI + 27.0 + mRotateOffset
    }

    fun updateLayoutPosition() {
        val parentWidth = mContainerWidth
        val parentHeight = mContainerHeight
        Log.d(TAG, "updateLayoutPosition: parentWidth:$parentWidth, parentHeight:$parentHeight")

        Log.d(TAG, "updateLayoutPosition: mTranslateX:$mTranslateX, mTranslateY:$mTranslateY")
        Log.d(TAG, "updateLayoutPosition: mAnchorOffsetX:$mAnchorOffsetX, mAnchorOffsetY:$mAnchorOffsetY")

        layoutX = (mParentOffsetX + mTranslateX) * parentWidth - mTargetView.fitWidth * mAnchorOffsetX - mOperatorIconWidth * mWidthRatio
        layoutY = (mParentOffsetY + mTranslateY) * parentHeight - mTargetView.fitHeight * mAnchorOffsetY - mOperatorIconHeight * mHeightRatio
    }

    fun setTargetImage(image: Image) {
        mTargetView.image = image
        updateTargetImageViewSize()
        updateSize()
        updateRotateOffset()
    }

    fun setRatio(widthRatio: Double, heightRatio: Double) {
        mWidthRatio = widthRatio
        mHeightRatio = heightRatio

        updateTargetImageViewSize()
        updateOperatorsSize()
        updateSize()
        updateTargetImageViewLayout()
        updateLayoutPosition()
        updateRotateOffset()
    }

    private fun updateRotateOffset() {
        mRotateOffset = Math.atan2(mTargetView.fitHeight + mOperatorIconHeight * mHeightRatio, mTargetView.fitWidth +mOperatorIconWidth * mWidthRatio)
    }

    private fun updateSize() {
        val parentWidth = mContainerWidth
        val parentHeight = mContainerHeight

        val image = mTargetView.image
        val imageViewWidth = (image?.width ?: 0.0) * mWidthRatio
        val imageViewHeight = (image?.height ?: 0.0) * mHeightRatio

        val width = mOperatorIconWidth * mWidthRatio * 2 + imageViewWidth + parentWidth * mTargetScaleX
        val height = mOperatorIconHeight * mHeightRatio * 2 + imageViewHeight + parentHeight * mTargetScaleY
        setSize(width, height)
        Log.d(TAG, "updateSize: width:$width, height:$height")
    }

    fun updateOperatorsSize() {
        listOf(mScaleIcon, mRotateIcon, mCloseIcon, mTranslateIcon).forEach {
            it.fitWidth = mOperatorIconWidth * mWidthRatio
            it.fitHeight = mOperatorIconHeight * mHeightRatio
        }
    }

    fun updateTargetImageViewSize() {
        val parentWidth = mContainerWidth
        val parentHeight = mContainerHeight

        val image = mTargetView.image ?: let {
            Log.d(TAG, "updateTargetImageViewSize: mTargetView.image is null")
            return
        }


        Log.d(TAG, "setTargetImage: image.width:${image.width}, widthRatio:${mWidthRatio}, image.height:${image.height}, heightRatio:${mHeightRatio}")
        mTargetView.fitWidth = image.width * mWidthRatio + parentWidth * mTargetScaleX
        mTargetView.fitHeight = image.height * mHeightRatio + parentHeight * mTargetScaleY
    }

    private fun updateTargetImageViewLayout() {
        mTargetView.layoutX = mOperatorIconWidth * mWidthRatio
        mTargetView.layoutY = mOperatorIconHeight * mHeightRatio
    }

    fun setOffsetInParent(x: Float, y: Float) {
        mParentOffsetX = x.toDouble()
        mParentOffsetY = y.toDouble()
    }

    fun setTargetViewAnchorOffsetIndex(anchorIndex: Int) {
        val offset = anchorOffsets[anchorIndex]
        mAnchorOffsetX = offset[0].toDouble()
        mAnchorOffsetY = offset[1].toDouble()
    }

    fun setContainerSize(width: Double, height: Double) {
        mContainerWidth = width
        mContainerHeight = height
    }
}

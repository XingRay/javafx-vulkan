package com.github.xingray.renderer

import android.util.Log
import com.github.xingray.kotlin.NativeLibraryLoader
import java.io.File
import java.nio.ByteBuffer

class VulkanRenderer {

    companion object {
        init {
            val TAG = VulkanRenderer::class.simpleName

            val osName = System.getProperty("os.name")
            Log.d(TAG, "osName:${osName}: ")

            val tmpdir = System.getProperty("java.io.tmpdir")
            Log.d(TAG, "tmpdir: ${tmpdir}")

            val saveDir = File(tmpdir).resolve("makeup-manager")
            Log.d(TAG, "load library: ${saveDir.absolutePath}")
//            NativeLibraryLoader.loadLibrary(saveDir, "natives/vulkan-renderer/windows-x64/glew32.dll")
//            NativeLibraryLoader.loadLibrary(saveDir, "natives/vulkan-renderer/windows-x64/opencv_world490.dll")
            NativeLibraryLoader.loadLibrary(saveDir, "natives/vulkan-renderer/windows-x64/vulkan_renderer.dll")
            Log.d(TAG, "load library finished: glew32 MyWindow")
        }
    }

    private var mNativePointer: Long = 0

    init {
        mNativePointer = newVulkanRenderer()
    }

    fun release() {
        deleteVulkanRenderer(mNativePointer)
        mNativePointer = 0
    }

    fun createMappingByteBuffer(): ByteBuffer {
        return nativeCreateMappingByteBuffer(mNativePointer);
    }

    fun drawFrame() {
        nativeDrawFrame(mNativePointer)
    }

    fun copyImageToBuffer() {
        nativeCopyImageToBuffer(mNativePointer)
    }


    private external fun newVulkanRenderer(): Long
    private external fun deleteVulkanRenderer(pointer: Long)

    private external fun nativeCreateMappingByteBuffer(pointer: Long): ByteBuffer
    private external fun nativeDrawFrame(pointer: Long)
    private external fun nativeCopyImageToBuffer(pointer: Long)
}
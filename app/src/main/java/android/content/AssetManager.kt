package android.content

import android.util.Log
import com.github.xingray.kotlin.file.ensureDirExists
import java.io.File
import java.net.JarURLConnection


class AssetManager {

    private val TAG = "AssetManager"

    fun copyAssetFolder(srcDir: String, targetDirPath: String) {
        Log.d(TAG, "copyAssetFolder: srcDir:${srcDir}, targetDirPath:${targetDirPath}")
        val srcResource = AssetManager::class.java.classLoader.getResource(srcDir) ?: let {
            Log.d(TAG, "copyAssetFolder: srcDir can not found, srcDir:${srcDir}, targetDirPath:${targetDirPath}")
            return
        }
        val path = srcResource.path
        val protocol = srcResource.protocol
        val file = srcResource.file

        Log.d(TAG, "copyAssetFolder: srcResource:${srcResource}")
        Log.d(TAG, "copyAssetFolder: srcResource.path:$path")
        Log.d(TAG, "copyAssetFolder: srcResource.protocol:$protocol")
        Log.d(TAG, "copyAssetFolder: srcResource.file:$file")

        if (protocol == "file") {
            // 开发其直接通过idea运行编译的 class 文件时, 读取的是 build/resource/main 目录下的文件
            File(path).copyRecursively(File(targetDirPath), true)
        } else if (protocol == "jar") {
            // 执行 java -jar app.jar 运行时, 读取的是 app.jar 中的文件
            val jarUrlConnection = srcResource.openConnection() as JarURLConnection
            val jarFile = jarUrlConnection.jarFile ?: let {
                Log.e(TAG, "copyAssetFolder: jarUrlConnection.jarFile is null")
                return
            }

            jarFile.entries().asIterator().forEach { jarEntry ->
                val name = jarEntry.name
                if (name.startsWith(srcDir)) {
                    Log.d(TAG, "copyAssetFolder: jarEntry:${jarEntry.name}")

                    if (jarEntry.isDirectory) {
                        // 目录, 注意 entryName 中包含了目录名, 复制到目标目录时,重复的目录名要去除
                        File(targetDirPath, name.substring(srcDir.length)).ensureDirExists()
                    } else {
                        // 文件
                        // 创建文件 注意 entryName 中包含了目录名, 复制到目标目录时,重复的目录名要去除
                        val targetFile = File(targetDirPath, name.substring(srcDir.length))
                        targetFile.parentFile.mkdirs()
                        targetFile.createNewFile()

                        jarFile.getInputStream(jarEntry).use { input ->
                            targetFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                }
            }

        } else {
            throw UnsupportedOperationException("srcDir:${srcDir}, protocol:${protocol}")
        }
    }

    fun readIntArray(triangleIndicesAssetsFile: String): IntArray {
        return intArrayOf()
    }
}
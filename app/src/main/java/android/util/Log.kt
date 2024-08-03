package android.util

import android.util.logger.ConsoleLogger
import android.util.logger.FileLogger
import com.github.xingray.AppConfig
import com.github.xingray.kotlin.thread.info
import com.xingray.java.util.DateTimeUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Log {
    companion object {
        private const val TAG_MAX_LEN = 20
        private const val THREAD_INFO_MAX_LEN = 10

        private val logFile = AppConfig.logDir.resolve("${DateTimeUtil.nowToString("yyyyMMddhhmmss")}.log")

        private val loggers = arrayOf(
            ConsoleLogger(),
            FileLogger(logFile)
        )


        init {
            // 在每次启动时自动删除10天前的日志文件
            deleteOldLogFiles()
        }

        private fun deleteOldLogFiles() {
            val logDirectory = AppConfig.logDir
            val logFiles = logDirectory.listFiles { _, name -> name.endsWith(".log") }

            val formatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmss")
            val tenDaysAgo = LocalDate.now().minusDays(10)

            logFiles?.forEach { file ->
                val fileName = file.nameWithoutExtension
                val fileDate = LocalDate.parse(fileName, formatter)

                if (fileDate.isBefore(tenDaysAgo)) {
                    file.delete()
                    d("Log", "Deleted log file: ${file.name}")
                }
            }
        }

        @JvmStatic
        fun d(tag: String?, s: String) {
            val formattedTag = tag?.take(TAG_MAX_LEN)?.padEnd(TAG_MAX_LEN) ?: " ".repeat(TAG_MAX_LEN)
            val message = "${Thread.currentThread().info()}\t${formattedTag}\t${s}"
            loggers.forEach { it.log(message) }
        }

        @JvmStatic
        fun e(tag: String?, s: String) {
            val formattedTag = tag?.take(TAG_MAX_LEN)?.padEnd(TAG_MAX_LEN) ?: " ".repeat(TAG_MAX_LEN)
            val message = "${Thread.currentThread().info()}\t${formattedTag}\t${s}"
            loggers.forEach { it.log(message) }
        }

        @JvmStatic
        fun w(tag: String?, s: String) {
            val formattedTag = tag?.take(TAG_MAX_LEN)?.padEnd(TAG_MAX_LEN) ?: " ".repeat(TAG_MAX_LEN)
            val message = "${Thread.currentThread().info()}\t${formattedTag}\t${s}"
            loggers.forEach { it.log(message) }
        }
    }
}
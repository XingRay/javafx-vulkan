package android.util.logger

import java.io.File
import java.io.FileWriter
import java.io.IOException


class FileLogger(private val logFile: File) : Logger {

    override fun log(message: String) {
        try {
            FileWriter(logFile, true).use { writer ->
                writer.write("$message\n")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
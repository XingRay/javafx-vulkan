package com.github.xingray

import com.github.xingray.kotlin.file.ensureDirExists
import java.io.File

object AppConfig {

    const val settingsCacheName = "setting"

    val appDataDir = File(System.getProperty("user.home"), "javafx_vulkan").ensureDirExists()

    val logDir = File(appDataDir, "log").ensureDirExists()

    val tmpDir = File(appDataDir, "tmp").ensureDirExists()
}
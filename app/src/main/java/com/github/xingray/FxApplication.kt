package com.github.xingray

import atlantafx.base.theme.PrimerLight
import com.github.xingray.base.BaseController
import com.github.xingray.page.start.StartController
import com.xingray.java.util.TaskExecutor
import com.xingray.javafx.base.BaseStage
import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage

class FxApplication : Application() {

    companion object {
        @JvmStatic
        private val TAG = FxApplication::class.java.simpleName
    }

    override fun start(stage: Stage?) {
        setUserAgentStylesheet(PrimerLight().getUserAgentStylesheet())

        TaskExecutor.setUiPoolExecutor { Platform.runLater(it) }

        val baseStage = BaseStage()
        baseStage.title = "javafx-vulkan"
        BaseController().openPage(StartController::class.java, baseStage)
    }
}
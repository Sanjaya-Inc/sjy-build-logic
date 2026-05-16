package com.sanjaya.buildlogic.tools

import com.sanjaya.buildlogic.core.utils.BasePlugin
import com.sanjaya.buildlogic.core.utils.get
import org.gradle.api.Project

class DetektConventionPlugin : BasePlugin() {

    override fun setup(target: Project) {
        val detektSetup: DetektSetup = target.get()
        detektSetup.setup()
    }
}

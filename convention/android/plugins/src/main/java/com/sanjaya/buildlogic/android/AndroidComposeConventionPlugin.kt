package com.sanjaya.buildlogic.android

import com.sanjaya.buildlogic.core.utils.BasePlugin
import com.sanjaya.buildlogic.core.utils.get
import org.gradle.api.Project

class AndroidComposeConventionPlugin : BasePlugin() {
    override fun setup(target: Project) {
        val composeSetup: AndroidComposeSetup = target.get()
        composeSetup.setup()
    }
}

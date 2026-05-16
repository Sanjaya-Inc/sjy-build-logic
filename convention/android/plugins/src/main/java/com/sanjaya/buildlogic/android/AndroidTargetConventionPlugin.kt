package com.sanjaya.buildlogic.android

import com.sanjaya.buildlogic.core.utils.BasePlugin
import com.sanjaya.buildlogic.core.utils.get
import org.gradle.api.Project

class AndroidTargetConventionPlugin : BasePlugin() {
    override fun setup(target: Project) {
        val androidTargetSetup: AndroidTargetSetup = target.get()
        androidTargetSetup.setup()
    }
}

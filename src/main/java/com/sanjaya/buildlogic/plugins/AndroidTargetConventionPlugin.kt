package com.sanjaya.buildlogic.plugins

import com.sanjaya.buildlogic.components.setup.AndroidTargetSetup
import org.gradle.api.Project
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class AndroidTargetConventionPlugin : BasePlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        val androidTargetSetup: AndroidTargetSetup by inject { parametersOf(target) }
        androidTargetSetup.setup()
    }
}

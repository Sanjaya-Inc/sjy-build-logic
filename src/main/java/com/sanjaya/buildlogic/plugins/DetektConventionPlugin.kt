package com.sanjaya.buildlogic.plugins

import com.sanjaya.buildlogic.components.setup.DetektSetup
import org.gradle.api.Project
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class DetektConventionPlugin: BasePlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        val detektSetup: DetektSetup by inject { parametersOf(target) }
        detektSetup.setup()
    }
}

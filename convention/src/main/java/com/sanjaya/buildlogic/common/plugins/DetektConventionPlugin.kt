package com.sanjaya.buildlogic.common.plugins

import com.sanjaya.buildlogic.common.components.DetektSetup
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

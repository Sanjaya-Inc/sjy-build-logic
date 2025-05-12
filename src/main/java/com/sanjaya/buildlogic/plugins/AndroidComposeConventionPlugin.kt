package com.sanjaya.buildlogic.plugins

import com.sanjaya.buildlogic.components.setup.AndroidComposeSetup
import org.gradle.api.Project
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class AndroidComposeConventionPlugin : BasePlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        val composeSetup: AndroidComposeSetup by inject { parametersOf(target) }
        composeSetup.setup()
    }
}

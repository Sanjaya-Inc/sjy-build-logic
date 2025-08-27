package com.sanjaya.buildlogic.plugins

import com.sanjaya.buildlogic.components.setup.TestSetup
import org.gradle.api.Project
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class TestConventionPlugin : BasePlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        val testConventionPlugin: TestSetup by inject { parametersOf(target) }
        testConventionPlugin.setup()
    }
}

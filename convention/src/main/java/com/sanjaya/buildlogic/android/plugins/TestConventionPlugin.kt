package com.sanjaya.buildlogic.android.plugins

import com.sanjaya.buildlogic.android.components.setup.TestSetup
import com.sanjaya.buildlogic.common.plugins.BasePlugin
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

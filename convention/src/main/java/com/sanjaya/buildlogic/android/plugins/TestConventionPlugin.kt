package com.sanjaya.buildlogic.android.plugins

import com.sanjaya.buildlogic.android.components.setup.TestSetup
import com.sanjaya.buildlogic.common.plugins.BasePlugin
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project

class TestConventionPlugin : BasePlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        val testConventionPlugin: TestSetup = ComponentProvider.provide(target)
        testConventionPlugin.setup()
    }
}

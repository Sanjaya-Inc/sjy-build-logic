package com.sanjaya.buildlogic.android

import com.sanjaya.buildlogic.core.utils.BasePlugin
import com.sanjaya.buildlogic.core.utils.get
import org.gradle.api.Project

class TestConventionPlugin : BasePlugin() {

    override fun setup(target: Project) {
        val testConventionPlugin: TestSetup = target.get()
        testConventionPlugin.setup()
    }
}

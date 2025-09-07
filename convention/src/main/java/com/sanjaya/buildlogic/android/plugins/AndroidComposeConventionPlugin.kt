package com.sanjaya.buildlogic.android.plugins

import com.sanjaya.buildlogic.android.components.setup.AndroidComposeSetup
import com.sanjaya.buildlogic.common.plugins.BasePlugin
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project

class AndroidComposeConventionPlugin : BasePlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        val composeSetup: AndroidComposeSetup = ComponentProvider.provide(target)
        composeSetup.setup()
    }
}

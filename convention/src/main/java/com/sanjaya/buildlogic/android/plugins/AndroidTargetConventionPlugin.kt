package com.sanjaya.buildlogic.android.plugins

import com.sanjaya.buildlogic.android.components.setup.AndroidTargetSetup
import com.sanjaya.buildlogic.common.plugins.BasePlugin
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project

class AndroidTargetConventionPlugin : BasePlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        val androidTargetSetup: AndroidTargetSetup = ComponentProvider.provide(target)
        androidTargetSetup.setup()
    }
}

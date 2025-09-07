package com.sanjaya.buildlogic.android.plugins

import com.sanjaya.buildlogic.android.components.setup.AndroidConventionSetup
import com.sanjaya.buildlogic.common.plugins.BasePlugin
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project

class AndroidLibConventionPlugin : BasePlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        val androidConventionSetup: AndroidConventionSetup = ComponentProvider.provide(target)
        androidConventionSetup.setupAndroidLibrary()
    }
}

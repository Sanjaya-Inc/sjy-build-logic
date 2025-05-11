package com.sanjaya.buildlogic.plugins

import com.sanjaya.buildlogic.components.plugin.PluginApplicator
import com.sanjaya.buildlogic.components.setup.AndroidConventionSetup
import com.sanjaya.buildlogic.utils.applyAndroidConvention
import org.gradle.api.Project
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class AndroidAppConventionPlugin : BasePlugin() {

    override fun apply(target: Project) {
        super.apply(target)
        val androidConventionSetup: AndroidConventionSetup by inject { parametersOf(target) }
        androidConventionSetup.setupAndroidApp()
    }
}

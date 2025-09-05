package com.sanjaya.buildlogic.android.plugins

import com.sanjaya.buildlogic.android.components.setup.AndroidConventionSetup
import com.sanjaya.buildlogic.common.plugins.BasePlugin
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

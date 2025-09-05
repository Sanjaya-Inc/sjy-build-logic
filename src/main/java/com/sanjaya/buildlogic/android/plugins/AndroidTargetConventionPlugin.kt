package com.sanjaya.buildlogic.android.plugins

import com.sanjaya.buildlogic.android.components.setup.AndroidTargetSetup
import com.sanjaya.buildlogic.common.plugins.BasePlugin
import org.gradle.api.Project
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class AndroidTargetConventionPlugin : BasePlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        val androidTargetSetup: AndroidTargetSetup by inject { parametersOf(target) }
        androidTargetSetup.setup()
    }
}

package com.sanjaya.buildlogic.android.plugins

import com.sanjaya.buildlogic.android.components.setup.AndroidConventionSetup
import com.sanjaya.buildlogic.common.plugins.BasePlugin
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project
import org.koin.core.annotation.Factory

@Factory
class AndroidAppConventionPlugin : BasePlugin() {

    override fun apply(target: Project) {
        super.apply(target)
        val setup = ComponentProvider.provide<AndroidConventionSetup>(target)
        setup.setupAndroidApp()
    }
}

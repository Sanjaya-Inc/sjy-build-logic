package com.sanjaya.buildlogic.multiplatform.plugins

import com.sanjaya.buildlogic.common.components.PluginApplicator
import com.sanjaya.buildlogic.common.plugins.BasePlugin
import com.sanjaya.buildlogic.multiplatform.components.setup.KmpCommonSetup
import com.sanjaya.buildlogic.multiplatform.components.setup.KmpKoinSetup
import org.gradle.api.Project
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class KmpLibConventionPlugin : BasePlugin() {

    override fun apply(target: Project) = with(target) {
        super.apply(target)
        val pluginApplicator: PluginApplicator by inject { parametersOf(target) }
        pluginApplicator.applyPluginsByAliases("android-library")
        val kmpCommonSetup: KmpCommonSetup by inject { parametersOf(target) }
        val kmpKoinSetup: KmpKoinSetup by inject { parametersOf(target) }
        kmpCommonSetup.setup()
        kmpKoinSetup.setup()
    }
}

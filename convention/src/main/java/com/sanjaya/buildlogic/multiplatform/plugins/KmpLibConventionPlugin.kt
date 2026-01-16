package com.sanjaya.buildlogic.multiplatform.plugins

import com.sanjaya.buildlogic.common.components.PluginApplicator
import com.sanjaya.buildlogic.common.plugins.BasePlugin
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import com.sanjaya.buildlogic.multiplatform.components.setup.KmpCommonSetup
import com.sanjaya.buildlogic.multiplatform.components.setup.KmpKoinSetup
import org.gradle.api.Project

class KmpLibConventionPlugin : BasePlugin() {

    override fun apply(target: Project) = with(target) {
        super.apply(target)
        val pluginApplicator: PluginApplicator = ComponentProvider.provide(target)
        pluginApplicator.applyPluginsByAliases("android-kotlin-multiplatform-library ")
        val kmpCommonSetup: KmpCommonSetup = ComponentProvider.provide(target)
        val kmpKoinSetup: KmpKoinSetup = ComponentProvider.provide(target)
        kmpCommonSetup.setup()
        kmpKoinSetup.setup()
    }
}

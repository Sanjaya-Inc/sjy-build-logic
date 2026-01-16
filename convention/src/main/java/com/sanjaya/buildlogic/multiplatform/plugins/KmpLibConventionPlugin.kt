package com.sanjaya.buildlogic.multiplatform.plugins

import com.sanjaya.buildlogic.common.plugins.BasePlugin
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import com.sanjaya.buildlogic.multiplatform.components.setup.KmpCommonSetup
import org.gradle.api.Project

class KmpLibConventionPlugin : BasePlugin() {

    override fun apply(target: Project) = with(target) {
        super.apply(target)
        val kmpCommonSetup: KmpCommonSetup = ComponentProvider.provide(target)
        kmpCommonSetup.setup()
    }
}

package com.sanjaya.buildlogic.common.plugins

import com.sanjaya.buildlogic.common.components.DetektSetup
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project

class DetektConventionPlugin : BasePlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        val detektSetup: DetektSetup = ComponentProvider.provide(target)
        detektSetup.setup()
    }
}

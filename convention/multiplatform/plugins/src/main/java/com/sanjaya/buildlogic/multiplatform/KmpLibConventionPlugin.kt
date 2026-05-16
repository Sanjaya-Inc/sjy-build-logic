package com.sanjaya.buildlogic.multiplatform

import com.sanjaya.buildlogic.core.utils.BasePlugin
import com.sanjaya.buildlogic.core.utils.get
import org.gradle.api.Project

class KmpLibConventionPlugin : BasePlugin() {

    override fun setup(target: Project) {
        val kmpCommonSetup: KmpCommonSetup = target.get()
        kmpCommonSetup.setup()
    }
}

package com.sanjaya.buildlogic.multiplatform

import com.sanjaya.buildlogic.core.utils.BasePlugin
import com.sanjaya.buildlogic.core.utils.get
import org.gradle.api.Project

class CmpConventionPlugin : BasePlugin() {

    override fun setup(target: Project) {
        val cmpSetup: CmpSetup = target.get()
        cmpSetup.setup()
    }
}

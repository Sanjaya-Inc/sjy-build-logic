package com.sanjaya.buildlogic.android

import com.sanjaya.buildlogic.core.utils.BasePlugin
import com.sanjaya.buildlogic.core.utils.get
import org.gradle.api.Project
import org.koin.core.annotation.Factory

@Factory
class AndroidAppConventionPlugin : BasePlugin() {

    override fun setup(target: Project) {
        val setup: AndroidConventionSetup = target.get()
        setup.setupAndroidApp()
    }
}

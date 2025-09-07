package com.sanjaya.buildlogic.android.plugins

import com.sanjaya.buildlogic.android.components.setup.FirebaseSetup
import com.sanjaya.buildlogic.common.plugins.BasePlugin
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project

class FirebasePlugin : BasePlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        val firebaseSetup: FirebaseSetup = ComponentProvider.provide(target)
        firebaseSetup.setup()
    }
}

package com.sanjaya.buildlogic.android.plugins

import com.sanjaya.buildlogic.android.components.setup.FirebaseSetup
import com.sanjaya.buildlogic.common.plugins.BasePlugin
import org.gradle.api.Project
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class FirebasePlugin : BasePlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        val firebaseSetup: FirebaseSetup by inject { parametersOf(target) }
        firebaseSetup.setup()
    }
}

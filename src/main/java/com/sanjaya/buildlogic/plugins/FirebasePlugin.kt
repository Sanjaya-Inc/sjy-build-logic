package com.sanjaya.buildlogic.plugins

import com.sanjaya.buildlogic.components.setup.FirebaseSetup
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

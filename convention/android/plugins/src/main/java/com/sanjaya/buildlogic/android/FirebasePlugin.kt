package com.sanjaya.buildlogic.android

import com.sanjaya.buildlogic.core.utils.BasePlugin
import com.sanjaya.buildlogic.core.utils.get
import org.gradle.api.Project

class FirebasePlugin : BasePlugin() {

    override fun setup(target: Project) {
        val firebaseSetup: FirebaseSetup = target.get()
        firebaseSetup.setup()
    }
}

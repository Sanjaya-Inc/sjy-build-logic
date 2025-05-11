package com.sanjaya.buildlogic.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.sanjaya.buildlogic.components.setup.AndroidComposeSetup
import com.sanjaya.buildlogic.components.setup.AndroidConventionSetup
import com.sanjaya.buildlogic.utils.findPlugin
import com.sanjaya.buildlogic.utils.isApp
import com.sanjaya.buildlogic.utils.isAppOrLib
import com.sanjaya.buildlogic.utils.isLib
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.sanjaya.buildlogic.utils.applyComposeDependencies
import org.gradle.kotlin.dsl.the
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class AndroidComposeConventionPlugin : BasePlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        val composeSetup: AndroidComposeSetup by inject { parametersOf(target) }
        composeSetup.setup()
    }
}

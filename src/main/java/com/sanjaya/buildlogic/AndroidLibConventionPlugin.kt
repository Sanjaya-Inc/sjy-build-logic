package com.sanjaya.buildlogic

import com.sanjaya.buildlogic.utils.addStartupDependencies
import com.sanjaya.buildlogic.utils.addStoreDependencies
import com.sanjaya.buildlogic.utils.applyKoin
import com.sanjaya.buildlogic.utils.applyKotlinPlugins
import com.sanjaya.buildlogic.utils.applyKspPlugins
import com.sanjaya.buildlogic.utils.applyDebuggingTools
import com.sanjaya.buildlogic.utils.applyPluginsWithLog
import com.sanjaya.buildlogic.utils.configureFlavors
import com.sanjaya.buildlogic.utils.testDependencies
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                applyPluginsWithLog("com.android.library")
                applyKotlinPlugins()
                applyKspPlugins()
                applyKoin()
                configureFlavors()
                applyPluginsWithLog("com.sanjaya.buildlogic.target")
                applyPluginsWithLog("com.sanjaya.buildlogic.detekt")
                testDependencies()
                applyDebuggingTools()
                addStoreDependencies()
                addStartupDependencies()
            }
        }
    }
}

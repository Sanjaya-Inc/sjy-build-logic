package com.sanjaya.buildlogic

import com.sanjaya.buildlogic.utils.applyPluginsWithLog
import com.sanjaya.buildlogic.utils.findLibs
import com.sanjaya.buildlogic.utils.findPlugin
import com.sanjaya.buildlogic.utils.implementationWithLog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ApolloConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                val apollo = findPlugin("apollo", true) ?: return
                applyPluginsWithLog(apollo)
            }
            dependencies {
                findLibs("apollo")?.let { implementationWithLog(it) }
            }
        }
    }
}

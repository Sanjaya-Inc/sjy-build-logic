package com.sanjaya.buildlogic.multiplatform.plugins

import com.sanjaya.buildlogic.common.components.DependenciesFinder
import com.sanjaya.buildlogic.common.components.PluginApplicator
import com.sanjaya.buildlogic.common.plugins.BasePlugin
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class CmpConventionPlugin : BasePlugin() {

    override fun apply(target: Project) = with(target) {
        super.apply(target)
        val dependenciesFinder: DependenciesFinder = ComponentProvider.provide(target)
        val pluginApplicator: PluginApplicator = ComponentProvider.provide(target)
        pluginApplicator.applyPluginsByAliases("compose-multiplatform", "kotlin-compose")
        configure<KotlinMultiplatformExtension> {
            sourceSets.androidMain.dependencies {
                val activity = dependenciesFinder.findLibrary("androidx-activity-compose")
                val preview = dependenciesFinder.findLibrary("androidx-ui-tooling-preview")
                implementation(preview)
                implementation(activity)
            }
            sourceSets.commonMain.dependencies {
                arrayOf(
                    "compose-runtime",
                    "compose-foundation",
                    "compose-material3-multiplatform",
                    "compose-ui-multiplatform",
                    "compose-components-resources",
                    "compose-components-ui-tooling-preview",
                    "orbit-mvi-core",
                    "orbit-mvi-viewmodel",
                    "orbit-mvi-compose",
                    "androidx-lifecycle-viewmodelCompose",
                    "androidx-lifecycle-runtimeCompose",
                    "navigation3-ui",
                    "navigation3-adaptive",
                    "navigation3-viewmodel"
                ).map(dependenciesFinder::findLibrary)
                    .forEach(this::implementation)
            }
        }
    }
}

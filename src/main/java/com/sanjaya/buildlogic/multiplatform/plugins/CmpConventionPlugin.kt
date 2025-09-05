package com.sanjaya.buildlogic.multiplatform.plugins

import com.sanjaya.buildlogic.common.components.DependenciesFinder
import com.sanjaya.buildlogic.common.components.PluginApplicator
import com.sanjaya.buildlogic.common.plugins.BasePlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class CmpConventionPlugin : BasePlugin() {

    override fun apply(target: Project) = with(target) {
        super.apply(target)
        val dependenciesFinder: DependenciesFinder by inject { parametersOf(target) }
        val pluginApplicator: PluginApplicator by inject { parametersOf(target) }
        pluginApplicator.applyPluginsByAliases("compose-multiplatform", "kotlin-compose")
        val compose = extensions.getByType<ComposeExtension>()
        configure<KotlinMultiplatformExtension> {
            sourceSets.androidMain.dependencies {
                val activity = dependenciesFinder.findLibrary("androidx-activity-compose")
                implementation(compose.dependencies.preview)
                implementation(activity)
            }
            sourceSets.commonMain.dependencies {
                implementation(compose.dependencies.runtime)
                implementation(compose.dependencies.foundation)
                implementation(compose.dependencies.material3)
                implementation(compose.dependencies.ui)
                implementation(compose.dependencies.components.resources)
                implementation(compose.dependencies.components.uiToolingPreview)
                arrayOf(
                    "orbit-mvi-core",
                    "orbit-mvi-viewmodel",
                    "orbit-mvi-compose",
                    "androidx-lifecycle-viewmodelCompose",
                    "androidx-lifecycle-runtimeCompose"
                ).map(dependenciesFinder::findLibrary)
                    .forEach(this::implementation)
            }
        }
        dependencies {
            add("debugImplementation", compose.dependencies.uiTooling)
        }
    }
}

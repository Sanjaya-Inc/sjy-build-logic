package com.sanjaya.buildlogic.multiplatform

import com.sanjaya.buildlogic.core.utils.DependenciesFinder
import com.sanjaya.buildlogic.core.utils.PluginApplicator
import com.sanjaya.buildlogic.core.utils.get
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class CmpSetupImpl(
    @InjectedParam private val project: Project,
    private val dependenciesFinder: DependenciesFinder = project.get(),
    private val pluginApplicator: PluginApplicator = project.get()
) : CmpSetup {
    override fun setup() {
        pluginApplicator.applyPluginsByAliases("compose-multiplatform", "kotlin-compose")
        project.configure<KotlinMultiplatformExtension> {
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

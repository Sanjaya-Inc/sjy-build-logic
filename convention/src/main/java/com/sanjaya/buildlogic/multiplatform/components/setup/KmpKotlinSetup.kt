package com.sanjaya.buildlogic.multiplatform.components.setup

import com.sanjaya.buildlogic.common.components.BuildLogicLogger
import com.sanjaya.buildlogic.common.components.DependenciesFinder
import com.sanjaya.buildlogic.common.components.PluginApplicator
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent

@Factory
class KmpKotlinSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val dependenciesFinder: DependenciesFinder = ComponentProvider.provide(project),
    private val pluginApplicator: PluginApplicator = ComponentProvider.provide(project)
) : KoinComponent {

    fun setup() {
        logger.title(TAG, "Setting up Kotlin for kmp project: ${project.name}")
        pluginApplicator.applyPluginsByIds(
            "kotlin-parcelize"
        )
        pluginApplicator.applyPluginsByAliases(
            "kotlin-serialization"
        )
        project.configure<KotlinMultiplatformExtension> {
            sourceSets.commonMain.dependencies {
                listOf(
                    "kotlin-serialization",
                    "coroutines-core",
                    "kotlin-immutable"
                ).map(dependenciesFinder::findLibrary).forEach(::implementation)
            }
        }
    }

    companion object {
        private const val TAG = "KmpKotlinSetup"
    }
}
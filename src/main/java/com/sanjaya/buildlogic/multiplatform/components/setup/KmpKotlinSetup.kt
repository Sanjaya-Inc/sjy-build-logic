package com.sanjaya.buildlogic.multiplatform.components.setup

import com.sanjaya.buildlogic.common.components.BuildLogicLogger
import com.sanjaya.buildlogic.common.components.DependenciesFinder
import com.sanjaya.buildlogic.common.components.PluginApplicator
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

@Factory
class KmpKotlinSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger
) : KoinComponent {

    private val dependenciesFinder: DependenciesFinder by inject { parametersOf(project) }
    private val pluginApplicator: PluginApplicator by inject { parametersOf(project) }

    fun setup() {
        logger.title(TAG, "Setting up Kotlin for kmp project: ${project.name}")
        pluginApplicator.applyPluginsByIds(
            "kotlin-parcelize"
        )
        pluginApplicator.applyPluginsByAliases(
            "kotlin-serialization"
        )
        val kotlinExt = project.extensions.getByType<KotlinProjectExtension>()
        project.configure<KotlinMultiplatformExtension>() {
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
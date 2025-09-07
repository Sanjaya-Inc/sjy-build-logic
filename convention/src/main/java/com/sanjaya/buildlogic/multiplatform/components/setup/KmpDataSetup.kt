package com.sanjaya.buildlogic.multiplatform.components.setup

import com.sanjaya.buildlogic.android.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.common.components.BuildLogicLogger
import com.sanjaya.buildlogic.common.components.DependenciesFinder
import com.sanjaya.buildlogic.common.components.PluginApplicator
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

@Factory
class KmpDataSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger
) : KoinComponent {

    private val pluginApplicator: PluginApplicator by inject { parametersOf(project) }
    private val dependenciesFinder: DependenciesFinder by inject { parametersOf(project) }

    fun setup() {
        logger.title(TAG, "Setting up KMP data layer for project: ${project.name}")
        remoteDependenciesSetup()
    }

    private fun remoteDependenciesSetup() = with(project) {
        pluginApplicator.applyPluginsByAliases("ktorfit")
        configure<KotlinMultiplatformExtension> {
            sourceSets.commonMain.dependencies {
                val multiplatformDeps = listOf(
                    "ktorfit",
                    "ktor-serialization",
                    "ktor-content-negotiation",
                    "ktor-logging",
                    "store",
                    "multiplatform-settings"
                ).map(dependenciesFinder::findLibrary)
                multiplatformDeps.forEach(::implementation)
            }
            sourceSets.androidMain.dependencies {
                implementation(
                    this@with.project.dependencies.platform(
                        dependenciesFinder.findLibrary("okhttp-bom")
                    )
                )
                listOf(
                    "okhttp",
                    "okhttp-log",
                    "ktor-okhttp"
                ).map(dependenciesFinder::findLibrary).forEach(::implementation)
            }
            sourceSets.iosMain.dependencies {
                implementation(dependenciesFinder.findLibrary("ktor-darwin"))
            }
        }
    }

    private companion object {
        const val TAG = "KmpDataSetup"
    }
}

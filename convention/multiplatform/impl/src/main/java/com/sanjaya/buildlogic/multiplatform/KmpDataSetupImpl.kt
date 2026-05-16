package com.sanjaya.buildlogic.multiplatform

import com.sanjaya.buildlogic.core.utils.BuildLogicLogger
import com.sanjaya.buildlogic.core.utils.DependenciesFinder
import com.sanjaya.buildlogic.core.utils.PluginApplicator
import com.sanjaya.buildlogic.core.utils.get
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class KmpDataSetupImpl(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val pluginApplicator: PluginApplicator = project.get(),
    private val dependenciesFinder: DependenciesFinder = project.get()
) : KmpDataSetup {

    override fun setup() {
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
                    "ktor-cio",
                    "ktor-logging",
                    "ktor-auth",
                    "store"
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
            targets.findByName("js")?.let {
                sourceSets.jsMain.dependencies {
                    implementation(dependenciesFinder.findLibrary("ktor-js"))
                }
            }
        }
    }

    private companion object {
        const val TAG = "KmpDataSetup"
    }
}

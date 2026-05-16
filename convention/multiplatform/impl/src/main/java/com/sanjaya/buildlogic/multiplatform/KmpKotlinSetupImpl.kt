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
import org.koin.core.component.KoinComponent

@Factory
class KmpKotlinSetupImpl(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val dependenciesFinder: DependenciesFinder = project.get(),
    private val pluginApplicator: PluginApplicator = project.get()
) : KmpKotlinSetup {

    override fun setup() {
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
                    "kotlin-immutable",
                    "kotlin-date",
                ).map(dependenciesFinder::findLibrary).forEach(::implementation)
            }
        }
    }

    companion object {
        private const val TAG = "KmpKotlinSetup"
    }
}
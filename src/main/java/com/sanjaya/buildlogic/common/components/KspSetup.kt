package com.sanjaya.buildlogic.common.components

import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

@Factory
class KspSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger
) : KoinComponent {

    private val pluginApplicator: PluginApplicator by inject { parametersOf(project) }

    fun setup() {
        logger.title(TAG, "Setting up ksp for project: ${project.name}")
        runCatching {
            pluginApplicator.applyPluginsByAliases("ksp")
            project.the<KotlinAndroidProjectExtension>().apply {
                sourceSets.named("main") {
                    kotlin.srcDir("build/generated/ksp/main/kotlin")
                }
                sourceSets.named("debug") {
                    kotlin.srcDir("build/generated/ksp/debug/kotlin")
                }
                sourceSets.named("release") {
                    kotlin.srcDir("build/generated/ksp/release/kotlin")
                }
            }
        }.onFailure {
            logger.i(
                TAG,
                "[ERROR] KSP Plugins is not applied, please add ksp on root build.gradle.kts first"
            )
            logger.i(TAG, "use this: alias(core.plugins.ksp) apply false")
        }.onSuccess {
            logger.i(TAG, "Successfully applied ksp plugin")
        }
    }

    private companion object {
        const val TAG = "KspSetup"
    }
}
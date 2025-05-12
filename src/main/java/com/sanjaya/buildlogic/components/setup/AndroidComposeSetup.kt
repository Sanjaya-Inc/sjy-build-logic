package com.sanjaya.buildlogic.components.setup

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.sanjaya.buildlogic.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.components.misc.BuildLogicLogger
import com.sanjaya.buildlogic.components.plugin.PluginApplicator
import com.sanjaya.buildlogic.utils.isApp
import com.sanjaya.buildlogic.utils.isAppOrLib
import com.sanjaya.buildlogic.utils.isLib
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

@Factory
class AndroidComposeSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger
) : KoinComponent {

    private val pluginApplicator: PluginApplicator by inject { parametersOf(project) }
    private val dependenciesApplicator: AndroidDependenciesApplicator by inject {
        parametersOf(
            project
        )
    }

    fun setup() {
        logger.title(TAG, "Setting up Android Compose for project: ${project.name}")
        with(project) {
            if (!isAppOrLib()) return@with
            val type =
                when {
                    isApp() -> ApplicationExtension::class
                    isLib() -> LibraryExtension::class
                    else -> null
                } ?: return@with
            pluginApplicator.applyPluginsByAliases("kotlin-compose")
            the(type).apply {
                buildFeatures {
                    compose = true
                }
            }
            dependenciesApplicator.implementations(
                "androidx-activity-compose",
                "androidx-ui",
                "androidx-ui-graphics",
                "androidx-ui-tooling-preview",
                "androidx-material3"
            )
            dependenciesApplicator.debugImplementations(
                "androidx-ui-tooling",
                "androidx-ui-test-manifest"
            )
        }
    }

    private companion object {
        const val TAG = "AndroidKotlinSetup"
    }
}

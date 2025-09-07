package com.sanjaya.buildlogic.android.components.setup

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.google.devtools.ksp.gradle.KspExtension
import com.sanjaya.buildlogic.android.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.android.utils.isApp
import com.sanjaya.buildlogic.android.utils.isAppOrLib
import com.sanjaya.buildlogic.android.utils.isLib
import com.sanjaya.buildlogic.common.components.BuildLogicLogger
import com.sanjaya.buildlogic.common.components.PluginApplicator
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
            the(KspExtension::class).apply {
                // used on some of the generated code, including default package name
                arg("compose-destinations.moduleName", project.name)
                // and if you want to generate mermaid graph files for this module's graphs:
                // (ideally use the same path for all modules, so that navigation in the html works well)
                arg("compose-destinations.htmlMermaidGraph", "$rootDir//navigation-docs")
                arg("compose-destinations.mermaidGraph", "$rootDir/navigation-docs")
            }
            dependenciesApplicator.implementations(
                "androidx-activity-compose",
                "androidx-ui",
                "androidx-ui-graphics",
                "androidx-ui-tooling-preview",
                "androidx-material3",
                "compose-google-fonts",
                "coil",
                "coil-okhttp",
                "compose-destination-core",
                "compose-destination-sheet",
            )
            dependenciesApplicator.ksp("compose-destination-ksp")
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

package com.sanjaya.buildlogic.android.components.setup

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.google.devtools.ksp.gradle.KspExtension
import com.sanjaya.buildlogic.android.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.android.utils.AndroidProjectTypeChecker
import com.sanjaya.buildlogic.common.components.BuildLogicLogger
import com.sanjaya.buildlogic.common.components.PluginApplicator
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent

@Factory
class AndroidComposeSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val pluginApplicator: PluginApplicator = ComponentProvider.provide(project),
    private val dependenciesApplicator: AndroidDependenciesApplicator = ComponentProvider.provide(
        project
    ),
    private val projectTypeChecker: AndroidProjectTypeChecker = ComponentProvider.provide(project),
) : KoinComponent {

    fun setup() = with(project) {
        this@AndroidComposeSetup.logger.title(
            TAG,
            "Setting up Android Compose for project: ${project.name}"
        )
        if (!projectTypeChecker.isAppOrLib()) return@with
        val type =
            when {
                projectTypeChecker.isApp() -> ApplicationExtension::class
                projectTypeChecker.isLib() -> LibraryExtension::class
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
            "compose-icons",
        )
        dependenciesApplicator.ksp("compose-destination-ksp")
        dependenciesApplicator.debugImplementations(
            "androidx-ui-tooling",
            "androidx-ui-test-manifest"
        )
    }

    private companion object {
        const val TAG = "AndroidKotlinSetup"
    }
}

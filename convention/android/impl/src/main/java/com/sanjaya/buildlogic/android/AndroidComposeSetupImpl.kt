package com.sanjaya.buildlogic.android

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.google.devtools.ksp.gradle.KspExtension
import com.sanjaya.buildlogic.core.android.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.android.utils.AndroidProjectTypeChecker
import com.sanjaya.buildlogic.core.utils.BuildLogicLogger
import com.sanjaya.buildlogic.core.utils.PluginApplicator
import com.sanjaya.buildlogic.core.utils.get
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent

@Factory
class AndroidComposeSetupImpl(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val pluginApplicator: PluginApplicator = project.get(),
    private val dependenciesApplicator: AndroidDependenciesApplicator = project.get(),
    private val projectTypeChecker: AndroidProjectTypeChecker = project.get(),
) : AndroidComposeSetup {

    override fun setup() = with(project) {
        this@AndroidComposeSetupImpl.logger.title(
            TAG,
            "Setting up Android Compose for project: ${project.name}"
        )
        if (!projectTypeChecker.isAppOrLib()) return@with
        pluginApplicator.applyPluginsByAliases("kotlin-compose")
        when {
            projectTypeChecker.isApp() -> {
                the<ApplicationExtension>().apply {
                    buildFeatures {
                        compose = true
                    }
                }
            }
            projectTypeChecker.isLib() -> {
                the<LibraryExtension>().apply {
                    buildFeatures {
                        compose = true
                    }
                }
            }
            else -> return@with
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

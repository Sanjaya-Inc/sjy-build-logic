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
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

@Factory
class TestSetup(
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
        logger.title(TAG, "Setting up test framework for project: ${project.name}")
        with(project) {
            dependenciesApplicator.testImplementationPlatform("koin-bom")
            dependenciesApplicator.testImplementations(
                "junit-jupiter",
                "mockk",
                "mockk-android",
                "mockk-agent",
                "turbine",
                "coroutines-test"
            )
            dependenciesApplicator.androidTestImplementations(
                "junit-jupiter",
                "mockk",
                "mockk-android",
                "mockk-agent",
                "turbine",
                "coroutines-test"
            )
            if (!isAppOrLib()) return@with
            val type =
                when {
                    isApp() -> ApplicationExtension::class
                    isLib() -> LibraryExtension::class
                    else -> null
                } ?: return@with
            the(type).apply {
                testOptions {
                    unitTests.all {
                        it.useJUnitPlatform()
                    }
                }
            }
        }
    }

    private companion object {
        const val TAG = "TestSetup"
    }
}

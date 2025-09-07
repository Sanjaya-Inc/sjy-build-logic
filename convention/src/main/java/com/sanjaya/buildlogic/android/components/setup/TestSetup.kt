package com.sanjaya.buildlogic.android.components.setup

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
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

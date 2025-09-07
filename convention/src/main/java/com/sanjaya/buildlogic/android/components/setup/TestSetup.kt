package com.sanjaya.buildlogic.android.components.setup

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.sanjaya.buildlogic.android.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.android.utils.AndroidProjectTypeChecker
import com.sanjaya.buildlogic.common.components.BuildLogicLogger
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent

@Factory
class TestSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val dependenciesApplicator: AndroidDependenciesApplicator = ComponentProvider.provide(
        project
    ),
    private val projectTypeChecker: AndroidProjectTypeChecker = ComponentProvider.provide(project),
) : KoinComponent {


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
            if (!projectTypeChecker.isAppOrLib()) return@with
            val type =
                when {
                    projectTypeChecker.isApp() -> ApplicationExtension::class
                    projectTypeChecker.isLib() -> LibraryExtension::class
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

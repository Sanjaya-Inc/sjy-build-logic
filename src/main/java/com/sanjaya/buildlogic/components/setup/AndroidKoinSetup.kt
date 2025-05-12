package com.sanjaya.buildlogic.components.setup

import com.google.devtools.ksp.gradle.KspExtension
import com.sanjaya.buildlogic.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.components.misc.BuildLogicLogger
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

@Factory
class AndroidKoinSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger
) : KoinComponent {

    private val dependenciesApplicator: AndroidDependenciesApplicator by inject {
        parametersOf(
            project
        )
    }

    fun setup() {
        logger.title(TAG, "Setting up Koin Android for project: ${project.name}")
        dependenciesApplicator.implementationPlatform("koin-bom")
        dependenciesApplicator.implementations(
            "koin-android",
            "koin-android-compat",
            "koin-androidx-workmanager",
            "koin-androidx-compose",
            "koin-androidx-compose-navigation",
            "koin-compose-viewmodel-navigation",
            "koin-compose-viewmodel-navigation",
            "koin-annotation"
        )
        dependenciesApplicator.ksp("koin-ksp")
        project.the<KspExtension>().apply {
            arg("KOIN_CONFIG_CHECK", "true")
        }
    }

    companion object {
        private const val TAG = "AndroidKoinSetup"
    }
}
package com.sanjaya.buildlogic.android.components.setup

import com.google.devtools.ksp.gradle.KspExtension
import com.sanjaya.buildlogic.android.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.common.components.BuildLogicLogger
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent

@Factory
class AndroidKoinSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val dependenciesApplicator: AndroidDependenciesApplicator = ComponentProvider.provide(
        project
    )
) : KoinComponent {

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
            arg("KOIN_CONFIG_CHECK", "false")
        }
    }

    companion object {
        private const val TAG = "AndroidKoinSetup"
    }
}
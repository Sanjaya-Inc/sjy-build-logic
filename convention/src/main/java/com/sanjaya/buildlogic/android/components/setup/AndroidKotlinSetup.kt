package com.sanjaya.buildlogic.android.components.setup

import com.sanjaya.buildlogic.android.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.common.components.BuildLogicLogger
import com.sanjaya.buildlogic.common.components.PluginApplicator
import org.gradle.api.Project
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

@Factory
class AndroidKotlinSetup(
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
        logger.title(TAG, "Setting up Kotlin for project: ${project.name}")
        pluginApplicator.applyPluginsByIds(
            "kotlin-android",
            "kotlin-parcelize",
        )
        pluginApplicator.applyPluginsByAliases(
            "kotlin-serialization"
        )
        dependenciesApplicator.implementations(
            "kotlin-serialization",
            "coroutines-core",
            "coroutines-android",
            "kotlin-immutable"
        )
    }

    private companion object {
        const val TAG = "AndroidKotlinSetup"
    }
}

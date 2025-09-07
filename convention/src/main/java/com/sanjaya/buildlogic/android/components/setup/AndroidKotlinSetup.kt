package com.sanjaya.buildlogic.android.components.setup

import com.sanjaya.buildlogic.android.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.common.components.BuildLogicLogger
import com.sanjaya.buildlogic.common.components.PluginApplicator
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent

@Factory
class AndroidKotlinSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val pluginApplicator: PluginApplicator = ComponentProvider.provide(project),
    private val dependenciesApplicator: AndroidDependenciesApplicator = ComponentProvider.provide(
        project
    )
) : KoinComponent {


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

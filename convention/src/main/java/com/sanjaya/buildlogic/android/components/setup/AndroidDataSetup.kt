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
class AndroidDataSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val pluginApplicator: PluginApplicator = ComponentProvider.provide(project),
    private val dependenciesApplicator: AndroidDependenciesApplicator = ComponentProvider.provide(
        project
    )
) : KoinComponent {

    fun setup() {
        logger.title(TAG, "Setting up android remote data for project: ${project.name}")
        remoteDependenciesSetup()
        localDependenciesSetup()
    }

    private fun remoteDependenciesSetup() {
        pluginApplicator.applyPluginsByAliases("ktorfit")
        dependenciesApplicator.implementationPlatform("okhttp-bom")
        dependenciesApplicator.implementations(
            "ktorfit",
            "okhttp",
            "okhttp-log",
            "ktor-okhttp",
            "ktor-serialization",
            "ktor-content-negotiation",
            "ktor-logging"
        )
    }

    private fun localDependenciesSetup() {
        dependenciesApplicator.implementations("mmkv")
        dependenciesApplicator.implementations("store")
    }

    private companion object {
        const val TAG = "KtorfitSetup"
    }
}

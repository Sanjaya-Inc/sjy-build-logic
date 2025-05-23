package com.sanjaya.buildlogic.components.setup

import com.sanjaya.buildlogic.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.components.misc.BuildLogicLogger
import com.sanjaya.buildlogic.components.plugin.PluginApplicator
import org.gradle.api.Project
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

@Factory
class AndroidDataSetup(
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

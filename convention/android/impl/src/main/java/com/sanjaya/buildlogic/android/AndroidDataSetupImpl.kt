package com.sanjaya.buildlogic.android

import com.sanjaya.buildlogic.core.android.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.core.utils.BuildLogicLogger
import com.sanjaya.buildlogic.core.utils.PluginApplicator
import com.sanjaya.buildlogic.core.utils.get
import org.gradle.api.Project
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class AndroidDataSetupImpl(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val pluginApplicator: PluginApplicator = project.get(),
    private val dependenciesApplicator: AndroidDependenciesApplicator = project.get()
) : AndroidDataSetup {

    override fun setup() {
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

package com.sanjaya.buildlogic.android.components.setup

import com.sanjaya.buildlogic.android.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.android.utils.AndroidProjectTypeChecker
import com.sanjaya.buildlogic.common.components.BuildLogicLogger
import com.sanjaya.buildlogic.common.components.PluginApplicator
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent

@Factory
class FirebaseSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val pluginApplicator: PluginApplicator = ComponentProvider.provide(project),
    private val dependenciesApplicator: AndroidDependenciesApplicator = ComponentProvider.provide(
        project
    ),
    private val projectTypeChecker: AndroidProjectTypeChecker = ComponentProvider.provide(project),
) : KoinComponent {


    fun setup() {
        logger.title(TAG, "Setting up firebase for project: ${project.name}")
        if (projectTypeChecker.isApp()) {
            pluginApplicator.applyPluginsByAliases("gms-services")
            pluginApplicator.applyPluginsByAliases("crashlytics")
        }
        dependenciesApplicator.implementationPlatform("firebase-bom")
        dependenciesApplicator.implementations(
            "firebase-analytics",
            "firebase-crashlytics",
            "firebase-messaging",
            "firebase-config",
            "firebase-auth"
        )
    }

    companion object {
        const val TAG = "FirebaseSetup"
    }
}
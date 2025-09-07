package com.sanjaya.buildlogic.android.components.setup

import com.sanjaya.buildlogic.android.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.android.utils.isApp
import com.sanjaya.buildlogic.common.components.BuildLogicLogger
import com.sanjaya.buildlogic.common.components.PluginApplicator
import org.gradle.api.Project
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.getValue

@Factory
class FirebaseSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger
): KoinComponent {

    private val pluginApplicator: PluginApplicator by inject { parametersOf(project) }
    private val dependenciesApplicator: AndroidDependenciesApplicator by inject {
        parametersOf(
            project
        )
    }

    fun setup() {
        logger.title(TAG, "Setting up firebase for project: ${project.name}")
        if (project.isApp()) {
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
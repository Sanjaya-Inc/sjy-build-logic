package com.sanjaya.buildlogic.android

import com.sanjaya.buildlogic.android.utils.AndroidProjectTypeChecker
import com.sanjaya.buildlogic.core.android.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.core.utils.BuildLogicLogger
import com.sanjaya.buildlogic.core.utils.PluginApplicator
import com.sanjaya.buildlogic.core.utils.get
import org.gradle.api.Project
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class FirebaseSetupImpl(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val pluginApplicator: PluginApplicator = project.get(),
    private val dependenciesApplicator: AndroidDependenciesApplicator = project.get(),
    private val projectTypeChecker: AndroidProjectTypeChecker = project.get(),
) : FirebaseSetup {

    override fun setup() {
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

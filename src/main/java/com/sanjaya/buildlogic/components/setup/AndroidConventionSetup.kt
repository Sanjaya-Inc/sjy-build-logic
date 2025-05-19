package com.sanjaya.buildlogic.components.setup

import com.sanjaya.buildlogic.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.components.plugin.PluginApplicator
import org.gradle.api.Project
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

@Factory
class AndroidConventionSetup(
    @InjectedParam private val project: Project
) : KoinComponent {

    private val pluginApplicator: PluginApplicator by inject { parametersOf(project) }
    private val kotlinSetup: AndroidKotlinSetup by inject { parametersOf(project) }
    private val kspSetup: KspSetup by inject { parametersOf(project) }
    private val androidKoinSetup: AndroidKoinSetup by inject { parametersOf(project) }
    private val androidDataSetup: AndroidDataSetup by inject { parametersOf(project) }
    private val firebaseSetup: FirebaseSetup by inject { parametersOf(project) }
    private val dependenciesApplicator: AndroidDependenciesApplicator by inject {
        parametersOf(
            project
        )
    }

    fun setupAndroidApp() {
        pluginApplicator.applyPluginsByIds("com.android.application")
        setupCommonConventions()
    }

    fun setupAndroidLibrary() {
        pluginApplicator.applyPluginsByIds("com.android.library")
        setupCommonConventions()
    }

    private fun setupCommonConventions() {
        kotlinSetup.setup()
        kspSetup.setup()
        androidKoinSetup.setup()
        androidDataSetup.setup()
        firebaseSetup.setup()
        dependenciesApplicator.implementations("app-startup")
        pluginApplicator.applyPluginsByIds("com.sanjaya.buildlogic.target")
        pluginApplicator.applyPluginsByIds("com.sanjaya.buildlogic.detekt")
    }
}

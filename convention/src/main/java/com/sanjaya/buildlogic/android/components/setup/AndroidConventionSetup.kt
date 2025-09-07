package com.sanjaya.buildlogic.android.components.setup

import com.sanjaya.buildlogic.android.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.common.components.KspSetup
import com.sanjaya.buildlogic.common.components.PluginApplicator
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent

@Factory
class AndroidConventionSetup(
    @InjectedParam private val project: Project,
    private val pluginApplicator: PluginApplicator = ComponentProvider.provide(project),
    private val kotlinSetup: AndroidKotlinSetup = ComponentProvider.provide(project),
    private val kspSetup: KspSetup = ComponentProvider.provide(project),
    private val androidKoinSetup: AndroidKoinSetup = ComponentProvider.provide(project),
    private val androidDataSetup: AndroidDataSetup = ComponentProvider.provide(project),
    private val dependenciesApplicator: AndroidDependenciesApplicator = ComponentProvider.provide(
        project
    )
) : KoinComponent {


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
        dependenciesApplicator.implementations("app-startup")
        pluginApplicator.applyPluginsByIds("com.sanjaya.buildlogic.target")
        pluginApplicator.applyPluginsByIds("com.sanjaya.buildlogic.detekt")
    }
}

package com.sanjaya.buildlogic.android

import com.sanjaya.buildlogic.core.android.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.core.utils.KspSetup
import com.sanjaya.buildlogic.core.utils.PluginApplicator
import com.sanjaya.buildlogic.core.utils.get
import org.gradle.api.Project
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class AndroidConventionSetupImpl(
    @InjectedParam private val project: Project,
    private val pluginApplicator: PluginApplicator = project.get(),
    private val kotlinSetup: AndroidKotlinSetupImpl = project.get(),
    private val kspSetup: KspSetup = project.get(),
    private val androidKoinSetup: AndroidKoinSetupImpl = project.get(),
    private val androidDataSetup: AndroidDataSetupImpl = project.get(),
    private val dependenciesApplicator: AndroidDependenciesApplicator = project.get()
) : AndroidConventionSetup {

    override fun setupAndroidApp() {
        pluginApplicator.applyPluginsByIds("com.android.application")
        setupCommonConventions()
    }

    override fun setupAndroidLibrary() {
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
    }
}

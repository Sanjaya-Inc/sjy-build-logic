package com.sanjaya.buildlogic.android

import com.sanjaya.buildlogic.core.android.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.core.utils.BuildLogicLogger
import com.sanjaya.buildlogic.core.utils.PluginApplicator
import com.sanjaya.buildlogic.core.utils.get
import org.gradle.api.Project
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent

@Factory
class AndroidKotlinSetupImpl(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val pluginApplicator: PluginApplicator = project.get(),
    private val dependenciesApplicator: AndroidDependenciesApplicator = project.get()
) : AndroidKotlinSetup {

    override fun setup() {
        logger.title(TAG, "Setting up Kotlin for project: ${project.name}")
        pluginApplicator.applyPluginsByIds(
            "kotlin-parcelize",
        )
        pluginApplicator.applyPluginsByAliases(
            "kotlin-serialization"
        )
        dependenciesApplicator.implementations(
            "kotlin-serialization",
            "coroutines-core",
            "coroutines-android",
            "kotlin-immutable",
            "kotlin-date",
        )
    }

    private companion object {
        const val TAG = "AndroidKotlinSetup"
    }
}

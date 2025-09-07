package com.sanjaya.buildlogic.common.components

import org.gradle.api.Project
import org.gradle.plugin.use.PluginDependency
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.jvm.optionals.getOrNull

@Factory
class PluginFinder(
    @InjectedParam private val project: Project,
    private val buildLogicLogger: BuildLogicLogger
) : KoinComponent {

    private val versionCatalogProvider: VersionCatalogProvider by inject { parametersOf(project) }

    fun find(alias: String): PluginDependency {
        var versionCatalog = ""
        val plugin = versionCatalogProvider.getAll().firstOrNull {
            it.findPlugin(alias).getOrNull() != null
        }?.also {
            versionCatalog = it.name
        }?.findPlugin(alias)?.getOrNull()?.orNull?.also {
            buildLogicLogger.i(TAG, "----> Found $alias on version catalog: $versionCatalog")
        }
        return requireNotNull(plugin) {
            "[$TAG]: Cannot find plugin with alias: $alias. Please check your version catalog."
        }
    }

    private companion object {
        const val TAG = "PluginFinder"
    }
}
package com.sanjaya.buildlogic.components.plugin

import com.sanjaya.buildlogic.components.misc.VersionCatalogProvider
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
) : KoinComponent {

    private val versionCatalogProvider: VersionCatalogProvider by inject { parametersOf(project) }

    fun find(alias: String): PluginDependency {
        val plugin = versionCatalogProvider.getAll().firstOrNull {
            it.findPlugin(alias).getOrNull() != null
        }?.findPlugin(alias)?.getOrNull()?.orNull
        return requireNotNull(plugin) {
            "[$TAG]: Cannot find plugin with alias: $alias. Please check your version catalog."
        }
    }

    private companion object {
        const val TAG = "PluginFinder"
    }
}

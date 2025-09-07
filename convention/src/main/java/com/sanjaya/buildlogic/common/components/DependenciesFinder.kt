package com.sanjaya.buildlogic.common.components

import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import kotlin.jvm.optionals.getOrNull

@Factory
class DependenciesFinder(
    @InjectedParam private val project: Project,
    private val buildLogicLogger: BuildLogicLogger,
    private val versionCatalogProvider: VersionCatalogProvider = ComponentProvider.provide(project)
) : KoinComponent {

    fun findLibrary(alias: String): Provider<MinimalExternalModuleDependency> {
        var versionCatalog = ""
        val library = versionCatalogProvider.getAll().firstOrNull {
            it.findLibrary(alias).getOrNull() != null
        }?.also {
            versionCatalog = it.name
        }?.findLibrary(alias)?.getOrNull()?.also {
            buildLogicLogger.i(TAG, "----> Found $alias on version catalog: $versionCatalog")
        }
        return requireNotNull(library) {
            "[$TAG]: Cannot find plugin with alias: $alias. Please check your version catalog."
        }
    }

    fun findBundle(alias: String): Provider<ExternalModuleDependencyBundle> {
        val bundle = versionCatalogProvider.getAll().firstOrNull {
            it.findBundle(alias).getOrNull() != null
        }?.findBundle(alias)?.getOrNull()
        return requireNotNull(bundle) {
            "[$TAG]: Cannot find plugin with alias: $alias. Please check your version catalog."
        }
    }

    companion object {
        private const val TAG = "DependenciesFinder"
    }
}
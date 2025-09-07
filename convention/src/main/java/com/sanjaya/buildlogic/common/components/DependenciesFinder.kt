package com.sanjaya.buildlogic.common.components

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.jvm.optionals.getOrNull

@Factory
class DependenciesFinder(
    @InjectedParam private val project: Project,
    private val buildLogicLogger: BuildLogicLogger
) : KoinComponent {

    private val versionCatalogProvider: VersionCatalogProvider by inject { parametersOf(project) }

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
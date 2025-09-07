package com.sanjaya.buildlogic.common.components

import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionConstraint
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import kotlin.jvm.optionals.getOrNull

@Factory
class VersionFinder(
    @InjectedParam private val project: Project,
    private val buildLogicLogger: BuildLogicLogger,
    private val versionCatalogProvider: VersionCatalogProvider = ComponentProvider.provide(project)
) : KoinComponent {

    fun find(alias: String): VersionConstraint {
        var versionCatalog = ""
        val version = versionCatalogProvider.getAll().firstOrNull {
            it.findVersion(alias).getOrNull() != null
        }?.also {
            versionCatalog = it.name
        }?.findVersion(alias)?.getOrNull()?.also {
            buildLogicLogger.i(TAG, "----> Found $alias on version catalog: $versionCatalog")
        }
        return requireNotNull(version) {
            "[$TAG]: Cannot find version with alias: $alias. Please check your version catalog."
        }
    }

    private companion object {
        const val TAG = "VersionFinder"
    }
}

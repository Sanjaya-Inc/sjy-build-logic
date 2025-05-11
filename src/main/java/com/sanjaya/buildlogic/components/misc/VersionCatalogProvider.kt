package com.sanjaya.buildlogic.components.misc

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class VersionCatalogProvider(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger
) {

    val libs
        get(): VersionCatalog = project.getVersionCatalogByName(LIBS_NAME)

    val core
        get(): VersionCatalog = project.getVersionCatalogByName(CORE_NAME)

    val ui
        get(): VersionCatalog = project.getVersionCatalogByName(UI_NAME)

    val essentials
        get(): VersionCatalog = project.getVersionCatalogByName(ESSENTIALS_NAME)

    fun getAll(): List<VersionCatalog> {
        return listOf(
            libs,
            core,
            ui,
            essentials
        )
    }

    private fun Project.getVersionCatalogByName(name: String): VersionCatalog {
        return runCatching {
            extensions.getByType<VersionCatalogsExtension>().named(name)
        }.getOrElse {
            this@VersionCatalogProvider.logger.i(TAG, "Cannot find version catalog named: $name")
            this@VersionCatalogProvider.logger.i(
                TAG,
                "please add version catalog on your setting.gradle.kts first"
            )
            throw it
        }
    }

    companion object {
        private const val TAG = "VersionCatalogProvider"
        private const val LIBS_NAME = "libs"
        private const val CORE_NAME = "core"
        private const val UI_NAME = "ui"
        private const val ESSENTIALS_NAME = "essentials"
    }
}

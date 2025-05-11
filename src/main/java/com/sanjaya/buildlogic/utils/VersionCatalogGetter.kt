package com.sanjaya.buildlogic.utils

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs
    get(): VersionCatalog = getVersionCatalogByName("libs")

val Project.core
    get(): VersionCatalog = getVersionCatalogByName("core")

val Project.ui
    get(): VersionCatalog = getVersionCatalogByName("ui")

val Project.essentials
    get(): VersionCatalog = getVersionCatalogByName("essentials")

private fun Project.getVersionCatalogByName(name: String): VersionCatalog {
    return runCatching {
        extensions.getByType<VersionCatalogsExtension>().named(name)
    }.getOrElse {
        printMessage("Cannot find version catalog named: $name")
        printMessage("please add version catalog on your setting.gradle.kts first")
        throw it
    }
}

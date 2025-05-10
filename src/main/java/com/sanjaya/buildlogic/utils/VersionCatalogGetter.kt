package com.sanjaya.buildlogic.utils

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.conventions
    get(): VersionCatalog = getVersionCatalogByName("conventions")

val Project.libs
    get(): VersionCatalog = getVersionCatalogByName("libs")

private fun Project.getVersionCatalogByName(name: String): VersionCatalog {
    return runCatching {
        extensions.getByType<VersionCatalogsExtension>().named(name)
    }.getOrElse {
        printMessage("Cannot find version catalog named: $name")
        printMessage("please add version catalog on your setting.gradle.kts first")
        throw it
    }
}

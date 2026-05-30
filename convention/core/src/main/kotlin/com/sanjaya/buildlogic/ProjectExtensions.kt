package com.sanjaya.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

fun Project.sjyVersion(alias: String): String {
    val catalogs = extensions.getByType<VersionCatalogsExtension>()
    return runCatching { catalogs.named("sjy").findVersion(alias).get().requiredVersion }
        .getOrElse { catalogs.named("libs").findVersion(alias).get().requiredVersion }
}

fun Project.sjyLibrary(alias: String): Any {
    val catalogs = extensions.getByType<VersionCatalogsExtension>()
    return runCatching { catalogs.named("sjy").findLibrary(alias).get().get() }
        .getOrElse { catalogs.named("libs").findLibrary(alias).get().get() }
}

fun Project.sjyBundle(alias: String): Any {
    val catalogs = extensions.getByType<VersionCatalogsExtension>()
    return runCatching { catalogs.named("sjy").findBundle(alias).get().get() }
        .getOrElse { catalogs.named("libs").findBundle(alias).get().get() }
}

fun Project.sjyPlugin(alias: String): String {
    val catalogs = extensions.getByType<VersionCatalogsExtension>()
    return runCatching { catalogs.named("sjy").findPlugin(alias).get().get().pluginId }
        .getOrElse { catalogs.named("libs").findPlugin(alias).get().get().pluginId }
}

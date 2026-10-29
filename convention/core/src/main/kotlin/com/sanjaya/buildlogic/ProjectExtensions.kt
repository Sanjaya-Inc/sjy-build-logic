package com.sanjaya.buildlogic

import java.util.Properties
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

fun Project.sjyVersion(alias: String): String {
    val catalogs = extensions.getByType<VersionCatalogsExtension>()
    return runCatching { catalogs.named("sjy").findVersion(alias).orElseThrow().requiredVersion }
        .getOrElse { catalogs.named("libs").findVersion(alias).orElseThrow().requiredVersion }
}

fun Project.sjyVersionOrNull(alias: String): String? {
    val catalogs = extensions.getByType<VersionCatalogsExtension>()
    return catalogs.optionalVersion("sjy", alias) ?: catalogs.optionalVersion("libs", alias)
}

private fun VersionCatalogsExtension.optionalVersion(catalogName: String, alias: String): String? {
    val catalog = runCatching { named(catalogName) }.getOrNull() ?: return null
    val version = catalog.findVersion(alias)
    if (!version.isPresent) return null
    return version.orElseThrow().requiredVersion
}

fun Project.sjyLibrary(alias: String): Provider<MinimalExternalModuleDependency> {
    val catalogs = extensions.getByType<VersionCatalogsExtension>()
    return runCatching { catalogs.named("sjy").findLibrary(alias).orElseThrow() }
        .getOrElse { catalogs.named("libs").findLibrary(alias).orElseThrow() }
}

fun Project.sjyBundle(alias: String): Provider<ExternalModuleDependencyBundle> {
    val catalogs = extensions.getByType<VersionCatalogsExtension>()
    return runCatching { catalogs.named("sjy").findBundle(alias).orElseThrow() }
        .getOrElse { catalogs.named("libs").findBundle(alias).orElseThrow() }
}

fun Project.sjyPlugin(alias: String): String {
    val catalogs = extensions.getByType<VersionCatalogsExtension>()
    return runCatching { catalogs.named("sjy").findPlugin(alias).orElseThrow().get().pluginId }
        .getOrElse { catalogs.named("libs").findPlugin(alias).orElseThrow().get().pluginId }
}

fun Project.localProperty(key: String): Provider<String> {
    val propertiesFile = layout.settingsDirectory.file("local.properties")
    return providers.fileContents(propertiesFile).asBytes.map { bytes ->
        val raw = Properties().apply { load(bytes.inputStream()) }.getProperty(key)
        raw?.trim()?.trim('"').orEmpty()
    }.orElse("")
}

fun Project.quotedLocalProperty(key: String): Provider<String> =
    localProperty(key).map { "\"$it\"" }

fun Project.quotedNormalizedLocalUrl(key: String): Provider<String> =
    localProperty(key).map { raw ->
        require(raw.isNotBlank()) {
            "Missing $key in local.properties. Copy local.properties.example to local.properties."
        }
        val normalized = if (raw.endsWith("/")) raw else "$raw/"
        "\"$normalized\""
    }

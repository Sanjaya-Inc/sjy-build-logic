package com.sanjaya.buildlogic.utils

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import kotlin.jvm.optionals.getOrNull

fun DependencyHandlerScope.kaptWithLog(notation: Provider<MinimalExternalModuleDependency>) {
    add("kapt", notation)
    printMessage("Adding library kapt: ${notation.orNull?.name}")
}

fun DependencyHandlerScope.implementationWithLog(notation: Provider<MinimalExternalModuleDependency>) {
    add("implementation", notation)
    printMessage("Adding library implementation: ${notation.orNull?.name}")
}

fun DependencyHandlerScope.apiWithLog(notation: Provider<MinimalExternalModuleDependency>) {
    add("api", notation)
    printMessage("Adding library api: ${notation.orNull?.name}")
}

fun DependencyHandlerScope.kspWithLog(notation: Provider<MinimalExternalModuleDependency>) {
    add("ksp", notation)
    printMessage("Adding ksp implementation: ${notation.orNull?.name}")
}

fun DependencyHandlerScope.implementationPlatformWithLog(notation: Provider<MinimalExternalModuleDependency>) {
    add("implementation", platform(notation))
    printMessage("Adding platform library implementation: ${notation.orNull?.name}")
}

fun DependencyHandlerScope.apiPlatformWithLog(notation: Provider<MinimalExternalModuleDependency>) {
    add("api", platform(notation))
    printMessage("Adding platform library implementation: ${notation.orNull?.name}")
}

fun DependencyHandlerScope.androidTestImplementationPlatformWithLog(notation: Provider<MinimalExternalModuleDependency>) {
    add("androidTestImplementation", platform(notation))
    printMessage("Adding android test platform library implementation: ${notation.orNull?.name}")
}

fun DependencyHandlerScope.androidTestImplementationWithLog(notation: Provider<MinimalExternalModuleDependency>) {
    add("androidTestImplementation", notation)
    printMessage("Adding android test library implementation: ${notation.orNull?.name}")
}

fun DependencyHandlerScope.debugImplementationWithLog(notation: Provider<MinimalExternalModuleDependency>) {
    add("debugImplementation", notation)
    printMessage("Adding debug library implementation: ${notation.orNull?.name}")
}

fun DependencyHandlerScope.testImplementationWithLog(notation: Provider<MinimalExternalModuleDependency>) {
    add("testImplementation", notation)
    printMessage("Adding test library implementation: ${notation.orNull?.name}")
}

fun KotlinDependencyHandler.implementationWithLog(notation: Provider<MinimalExternalModuleDependency>) {
    implementation(notation)
    printMessage("Adding library implementation: ${notation.orNull?.name}")
}

fun KotlinDependencyHandler.apiWithLog(notation: Provider<MinimalExternalModuleDependency>) {
    api(notation)
    printMessage("Adding library api: ${notation.orNull?.name}")
}

fun KotlinDependencyHandler.kspWithLog(notation: Provider<MinimalExternalModuleDependency>) {
    project.dependencies.add("ksp", notation)
    printMessage("Adding ksp implementation: ${notation.orNull?.name}")
}

fun KotlinDependencyHandler.implementationPlatformWithLog(notation: Provider<MinimalExternalModuleDependency>) {
    implementation(project.dependencies.platform(notation))
    printMessage("Adding platform library implementation: ${notation.orNull?.name}")
}

fun KotlinDependencyHandler.apiPlatformWithLog(notation: Provider<MinimalExternalModuleDependency>) {
    api(project.dependencies.platform(notation))
    printMessage("Adding platform library implementation: ${notation.orNull?.name}")
}

fun Project.findLibs(
    alias: String,
    isPrintError: Boolean = false,
): Provider<MinimalExternalModuleDependency>? {
    val coreDependency = core.findLibrary(alias).getOrNull()
    if (coreDependency != null) {
        return coreDependency
    }

    val uiDependency = ui.findLibrary(alias).getOrNull()
    if (uiDependency != null) {
        return uiDependency
    }

    val essentialsDependency = essentials.findLibrary(alias)?.getOrNull()
    if (essentialsDependency != null) {
        return essentialsDependency
    }

    val libsDependency = libs.findLibrary(alias).getOrNull()
    if (libsDependency == null) {
        if (isPrintError) printMessage("Cannot find $alias on buildtools version catalog, please check version")
        return null
    }
    return libsDependency
}

fun Project.isAliasesExistOnBuildTools(vararg alias: String): Boolean {
    return alias.all {
        val corePlugin = core.findPlugin(it).getOrNull()?.orNull?.pluginId
        val coreLib = core.findLibrary(it).getOrNull()

        val uiPlugin = ui.findPlugin(it).getOrNull()?.orNull?.pluginId
        val uiLib = ui.findLibrary(it).getOrNull()

        val essentialsPlugin = essentials.findPlugin(it).getOrNull()?.orNull?.pluginId
        val essentialsLib = essentials.findLibrary(it).getOrNull()

        val libsPlugin = libs.findPlugin(it).getOrNull()?.orNull?.pluginId
        val libsLib = libs.findLibrary(it).getOrNull()

        corePlugin != null || coreLib != null ||
                uiPlugin != null || uiLib != null ||
                essentialsPlugin != null || essentialsLib != null ||
                libsPlugin != null || libsLib != null
    }
}

fun Project.testDependencies() {
    dependencies {
        findLibs("junit")?.also { testImplementationWithLog(it) }
        findLibs("androidx-junit")?.also { androidTestImplementationWithLog(it) }
        findLibs("androidx-espresso-core")?.also { androidTestImplementationWithLog(it) }
    }
}

fun Project.addStoreDependencies() {
    dependencies {
        findLibs("store")?.also { implementationWithLog(it) }
        findLibs("store-cache5")?.also { implementationWithLog(it) }
    }
}

fun Project.addStartupDependencies() {
    dependencies {
        findLibs("startup")?.also { implementationWithLog(it) }
    }
}

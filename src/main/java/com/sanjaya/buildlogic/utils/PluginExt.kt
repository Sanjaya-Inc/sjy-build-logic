package com.sanjaya.buildlogic.utils

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import kotlin.jvm.optionals.getOrNull

fun Project.isApp(): Boolean {
    return pluginManager.hasPlugin("com.android.application")
}

fun Project.isLib(): Boolean {
    return pluginManager.hasPlugin("com.android.library")
}

fun Project.isAppOrLib(): Boolean {
    val isApp = isApp()
    val isLib = isLib()
    return isApp || isLib
}

fun Project.applyKotlinPlugins() {
    with(pluginManager) {
        applyPluginsWithLog("kotlin-android")
        applyPluginsWithLog("kotlin-parcelize")
    }
    dependencies {
        findLibs("kotlin-serialization")?.let { implementationWithLog(it) }
        findLibs("kotlin-coroutines")?.let { implementationWithLog(it) }
    }
}

fun Project.applyAndroidConvention(pluginId: String) {
    with(pluginManager) {
        applyPluginsWithLog(pluginId)
        applyKotlinPlugins()
        applyKspPlugins()
        applyKoin()
        configureFlavors()
        applyPluginsWithLog("com.sanjaya.buildlogic.target")
        applyPluginsWithLog("com.sanjaya.buildlogic.detekt")
        testDependencies()
        applyDebuggingTools()
        addStoreDependencies()
        addStartupDependencies()
    }
}

fun PluginManager.applyPluginsWithLog(pluginId: String) {
    apply(pluginId)
    printMessage("Applying plugins: $pluginId")
}

fun Project.applyKspPlugins() {
    val kspPluginId = findPlugin("ksp", true) ?: return
    runCatching {
        pluginManager.apply(kspPluginId)
        the<KotlinAndroidProjectExtension>().apply {
            sourceSets.named("main") {
                kotlin.srcDir("build/generated/ksp/main/kotlin")
            }
            sourceSets.named("debug") {
                kotlin.srcDir("build/generated/ksp/debug/kotlin")
            }
            sourceSets.named("release") {
                kotlin.srcDir("build/generated/ksp/release/kotlin")
            }
        }
    }.onFailure {
        printMessage("KSP Plugins is not applied, please add ksp on root build.gradle.kts first")
        printMessage("use this: alias(conventions.plugins.ksp) apply false", true)
    }.onSuccess {
        printMessage("Applying plugins: KSP")
    }
}

fun Project.findPlugin(
    alias: String,
    isPrintError: Boolean = false,
): String? {
    val kspPluginId = core.findPlugin(alias).getOrNull()?.orNull?.pluginId
    if (kspPluginId == null) {
        if (isPrintError) printMessage("Cannot find $alias on version catalog, please check version")
        return null
    }
    return kspPluginId
}

fun Project.applyKoin() {
    dependencies {
        findLibs("koin-core")?.let { apiWithLog(it) }
        findLibs("koin-android")?.let { apiWithLog(it) }
        findLibs("koin-compose")?.let { apiWithLog(it) }
        findLibs("koin-androidx-compose")?.let { apiWithLog(it) }
        findLibs("koin-annotations")?.let { apiWithLog(it) }
        findLibs("koin-ksp-compiler")?.let { kspWithLog(it) }
        findLibs("koin-annotations-bom")?.let { apiPlatformWithLog(it) }
    }
    the<KspExtension>().apply {
        arg("KOIN_CONFIG_CHECK", "true")
    }
}

fun Project.configureKotlinMultiplatform(
    extension: KotlinMultiplatformExtension
) = extension.apply {
    jvmToolchain(21)

    // targets
    androidTarget()
    iosArm64()
    iosX64()
    iosSimulatorArm64()

    applyDefaultHierarchyTemplate()

    //common dependencies
    sourceSets.apply {
        commonMain {
            dependencies {
                findLibs("kotlin-coroutines")?.let { implementationWithLog(it) }
                findLibs("kotlin-datetime")?.let { implementationWithLog(it) }
                findLibs("kotlin-serialization")?.let { implementationWithLog(it) }

                findLibs("koin-core")?.let { implementationWithLog(it) }
                findLibs("koin-annotations-bom")?.let { apiPlatformWithLog(it) }
                findLibs("koin-annotations")?.let { apiWithLog(it) }
                findLibs("koin-ksp-compiler")?.let { kspWithLog(it) }

                findLibs("kermit")?.let { implementationWithLog(it) }
                findLibs("multiplatform-settings")?.let { implementationWithLog(it) }
                findLibs("multiplatform-settings-no-arg")?.let { implementationWithLog(it) }
            }
        }

        androidMain {
            dependencies {
                findLibs("koin-android")?.let { implementationWithLog(it) }
            }
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-annotations-common"))

            findLibs("koin-test")?.let { implementationWithLog(it) }
            findLibs("kotlin-coroutines-test")?.let { implementationWithLog(it) }
        }
    }
}

fun Project.applyComposeDependencies() = dependencies {
    findLibs("compose-bom")?.let { implementationPlatformWithLog(it) }
    findLibs("compose-bom")?.let { androidTestImplementationPlatformWithLog(it) }
    findLibs("compose-activity")?.let { implementationWithLog(it) }
    findLibs("compose-ui")?.let { implementationWithLog(it) }
    findLibs("compose-material")?.let { implementationWithLog(it) }
    findLibs("compose-ui-graphics")?.let { implementationWithLog(it) }
    findLibs("compose-ui-preview")?.let { implementationWithLog(it) }
    findLibs("compose-lifecycle-viewmodel")?.let { implementationWithLog(it) }
    findLibs("compose-lifecycle-runtime")?.let { implementationWithLog(it) }
    findLibs("compose-foundation")?.let { implementationWithLog(it) }
    findLibs("compose-lottie")?.let { implementationWithLog(it) }
    findLibs("compose-shimmer")?.let { implementationWithLog(it) }
    findLibs("compose-animation")?.let { implementationWithLog(it) }
    findLibs("compose-paging")?.let { implementationWithLog(it) }
    findLibs("compose-ui-tooling")?.let { debugImplementationWithLog(it) }
    findLibs("compose-ui-manifest")?.let { debugImplementationWithLog(it) }
    findLibs("compose-test-junit")?.let { androidTestImplementationWithLog(it) }
    findLibs("coil")?.let { implementationWithLog(it) }
    findLibs("coil-svg")?.let { implementationWithLog(it) }
    findLibs("coil-gif")?.let { implementationWithLog(it) }
    findLibs("lifecycle-ktx")?.let { implementationWithLog(it) }
    findLibs("lifecycle-runtime")?.let { implementationWithLog(it) }
    findLibs("lifecycle-process")?.let { implementationWithLog(it) }
}

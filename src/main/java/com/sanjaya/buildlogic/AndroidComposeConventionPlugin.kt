package com.sanjaya.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.sanjaya.buildlogic.utils.androidTestImplementationPlatformWithLog
import com.sanjaya.buildlogic.utils.androidTestImplementationWithLog
import com.sanjaya.buildlogic.utils.debugImplementationWithLog
import com.sanjaya.buildlogic.utils.findLibs
import com.sanjaya.buildlogic.utils.findPlugin
import com.sanjaya.buildlogic.utils.implementationPlatformWithLog
import com.sanjaya.buildlogic.utils.implementationWithLog
import com.sanjaya.buildlogic.utils.isApp
import com.sanjaya.buildlogic.utils.isAppOrLib
import com.sanjaya.buildlogic.utils.isLib
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            if (!isAppOrLib()) return@with
            val type =
                when {
                    isApp() -> ApplicationExtension::class
                    isLib() -> LibraryExtension::class
                    else -> null
                } ?: return@with
            val composePlugin = findPlugin("compose-compiler", true) ?: return
            pluginManager.apply(composePlugin)
            the(type).apply {
                buildFeatures {
                    compose = true
                }
            }
            applyComposeDepencencies()
        }
    }

    private fun Project.applyComposeDepencencies() = dependencies {
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
}

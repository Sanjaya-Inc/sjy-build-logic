package com.sanjaya.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension

println("[Build Logic][AndroidTargetSetup] Setting up Android Target for project: ${project.name}")

val isApp = pluginManager.hasPlugin("com.android.application")
val isLib = pluginManager.hasPlugin("com.android.library")

if (isApp || isLib) {
    val targetSdkVersion = project.sjyVersion("compile-sdk").toInt()
    val minSdkVersion = project.sjyVersion("min-sdk").toInt()
    val jvmTargetVersion = project.sjyVersion("jvm-target")

    println("[Build Logic][AndroidTargetSetup] Setting up target android sdk to: $targetSdkVersion")
    println("[Build Logic][AndroidTargetSetup] Setting up min android sdk to: $minSdkVersion")

    if (isApp) {
        configure<ApplicationExtension> {
            compileSdk = targetSdkVersion
            defaultConfig {
                multiDexEnabled = true
                targetSdk = targetSdkVersion
                minSdk = minSdkVersion
                println("[Build Logic][AndroidTargetSetup] Set Multidex enabled")
            }
            buildFeatures {
                buildConfig = false
            }
        }
    } else {
        configure<LibraryExtension> {
            compileSdk = targetSdkVersion
            defaultConfig {
                minSdk = minSdkVersion
            }
            buildFeatures {
                buildConfig = false
            }
        }
    }

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(jvmTargetVersion))
        }
    }

    dependencies {
        add("implementation", project.sjyLibrary("androidx-core-ktx"))
    }
}

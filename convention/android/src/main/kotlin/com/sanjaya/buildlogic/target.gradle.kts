package com.sanjaya.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CompileSdkSpec
import com.android.build.api.dsl.CompileSdkVersion
import com.android.build.api.dsl.LibraryExtension

println("[Build Logic][AndroidTargetSetup] Setting up Android Target for project: ${project.name}")

pluginManager.withPlugin("com.android.application") {
    configureAndroidSdk { compileSdkMajor, compileSdkMinor, minSdkVersion ->
        configure<ApplicationExtension> {
            compileSdk {
                version = releaseFromCatalog(compileSdkMajor, compileSdkMinor)
            }
            defaultConfig {
                targetSdk = compileSdkMajor
                minSdk = minSdkVersion
            }
            buildFeatures {
                buildConfig = false
            }
        }
    }
}

pluginManager.withPlugin("com.android.library") {
    configureAndroidSdk { compileSdkMajor, compileSdkMinor, minSdkVersion ->
        configure<LibraryExtension> {
            compileSdk {
                version = releaseFromCatalog(compileSdkMajor, compileSdkMinor)
            }
            defaultConfig {
                minSdk = minSdkVersion
            }
            buildFeatures {
                buildConfig = false
            }
        }
    }
}

fun Project.configureAndroidSdk(
    configureExtension: Project.(compileSdkMajor: Int, compileSdkMinor: Int?, minSdkVersion: Int) -> Unit,
) {
    val compileSdkMajor = sjyVersion("compile-sdk").toInt()
    val compileSdkMinor = sjyVersionOrNull("compile-sdk-minor")?.toInt()
    val minSdkVersion = sjyVersion("min-sdk").toInt()
    val jvmTargetVersion = sjyVersion("jvm-target")
    val compileSdkLabel = compileSdkMinor?.let { "$compileSdkMajor.$it" } ?: "$compileSdkMajor"
    println("[Build Logic][AndroidTargetSetup] Setting up compile android sdk to: $compileSdkLabel")
    println("[Build Logic][AndroidTargetSetup] Setting up target android sdk to: $compileSdkMajor")
    println("[Build Logic][AndroidTargetSetup] Setting up min android sdk to: $minSdkVersion")
    configureExtension(compileSdkMajor, compileSdkMinor, minSdkVersion)
    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(jvmTargetVersion))
        }
    }
    dependencies {
        add("implementation", sjyLibrary("androidx-core-ktx"))
    }
}

fun CompileSdkSpec.releaseFromCatalog(major: Int, minor: Int?): CompileSdkVersion =
    if (minor == null) {
        release(major)
    } else {
        release(major) { minorApiLevel = minor }
    }

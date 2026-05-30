package com.sanjaya.buildlogic.multiplatform

import com.sanjaya.buildlogic.sjyVersion
import com.sanjaya.buildlogic.sjyLibrary
import com.sanjaya.buildlogic.sjyPlugin
import com.sanjaya.buildlogic.sjyBundle
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

import com.android.build.api.variant.KotlinMultiplatformAndroidComponentsExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun String.toPascalCase(): String = split('-', '_')
    .joinToString("") { word -> word.replaceFirstChar { it.uppercaseChar() } }

println("[Build Logic][KmpCommonSetup] Setting up KMP Common for project: ${project.name}")

pluginManager.apply(project.sjyPlugin("kotlin-multiplatform"))
pluginManager.apply(project.sjyPlugin("android-kotlin-multiplatform-library"))

println("[Build Logic][KspSetup] Setting up ksp for project: ${project.name}")
runCatching {
    pluginManager.apply(project.sjyPlugin("ksp"))
    configure<KotlinAndroidProjectExtension> {
        sourceSets.named("main") { kotlin.srcDir("build/generated/ksp/main/kotlin") }
        sourceSets.named("debug") { kotlin.srcDir("build/generated/ksp/debug/kotlin") }
        sourceSets.named("release") { kotlin.srcDir("build/generated/ksp/release/kotlin") }
    }
}.onFailure {
    println("[Build Logic][KspSetup] [ERROR] KSP Plugins is not applied, please add ksp on root build.gradle.kts first")
}.onSuccess {
    println("[Build Logic][KspSetup] Successfully applied ksp plugin")
}

println("[Build Logic][KmpKotlinSetup] Setting up Kotlin for kmp project: ${project.name}")
pluginManager.apply("kotlin-parcelize")
pluginManager.apply(project.sjyPlugin("kotlin-serialization"))

pluginManager.apply(project.sjyPlugin("ktorfit"))

configure<KotlinMultiplatformAndroidComponentsExtension> {
    finalizeDsl {
        it.androidResources.enable = true
        it.minSdk = project.sjyVersion("min-sdk").toInt()
        it.compileSdk = project.sjyVersion("compile-sdk").toInt()
    }
}

configure<KotlinMultiplatformExtension> {
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = project.name.toPascalCase()
            isStatic = true
            val bundleId = project.findProperty("ios.bundleId") as? String
            if (bundleId != null) {
                freeCompilerArgs += listOf("-Xbinary=bundleId=$bundleId")
                println("[Build Logic][KmpCommonSetup] Using bundleId=$bundleId for ${project.name}")
            } else {
                println("[Build Logic][KmpCommonSetup] No ios.bundleId found for ${project.name}, using default")
            }
        }
    }

    sourceSets.commonMain.dependencies {
        implementation(project.sjyLibrary("napier"))

        implementation(project.sjyLibrary("kotlin-serialization"))
        implementation(project.sjyLibrary("coroutines-core"))
        implementation(project.sjyLibrary("kotlin-immutable"))
        implementation(project.sjyLibrary("kotlin-date"))

        implementation(project.dependencies.platform(project.sjyLibrary("koin-bom")))
        implementation(project.sjyLibrary("koin-core"))
        implementation(project.sjyLibrary("koin-ktor"))
        implementation(project.sjyLibrary("koin-compose"))
        implementation(project.sjyLibrary("koin-compose-viewmodel"))
        implementation(project.sjyLibrary("koin-annotation"))

        implementation(project.sjyLibrary("ktorfit"))
        implementation(project.sjyLibrary("ktor-serialization"))
        implementation(project.sjyLibrary("ktor-content-negotiation"))
        implementation(project.sjyLibrary("ktor-cio"))
        implementation(project.sjyLibrary("ktor-logging"))
        implementation(project.sjyLibrary("ktor-auth"))
        implementation(project.sjyLibrary("store"))
    }

    sourceSets.commonTest.dependencies {
        implementation(project.sjyLibrary("kotlin-test"))
    }

    sourceSets.androidMain.dependencies {
        implementation(project.dependencies.platform(project.sjyLibrary("okhttp-bom")))
        implementation(project.sjyLibrary("okhttp"))
        implementation(project.sjyLibrary("okhttp-log"))
        implementation(project.sjyLibrary("ktor-okhttp"))

        implementation(project.dependencies.platform(project.sjyLibrary("koin-bom")))
        implementation(project.sjyLibrary("koin-android"))
        implementation(project.sjyLibrary("koin-android-compat"))
        implementation(project.sjyLibrary("koin-androidx-workmanager"))
        implementation(project.sjyLibrary("koin-androidx-compose"))
        implementation(project.sjyLibrary("koin-androidx-compose-navigation"))
        implementation(project.sjyLibrary("koin-compose-viewmodel-navigation"))
        implementation(project.sjyLibrary("koin-annotation"))
    }

    sourceSets.iosMain.dependencies {
        implementation(project.sjyLibrary("ktor-darwin"))
    }

    targets.findByName("js")?.let {
        sourceSets.getByName("jsMain").dependencies {
            implementation(project.sjyLibrary("ktor-js"))
        }
    }
}

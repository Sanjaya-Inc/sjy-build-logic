package com.sanjaya.buildlogic.multiplatform

import com.sanjaya.buildlogic.sjyLibrary
import com.sanjaya.buildlogic.sjyPlugin

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

println("[Build Logic][CmpSetup] Setting up Compose Multiplatform for project: ${project.name}")

pluginManager.apply(project.sjyPlugin("compose-multiplatform"))
pluginManager.apply(project.sjyPlugin("kotlin-compose"))

configure<KotlinMultiplatformExtension> {
    sourceSets.androidMain.dependencies {
        implementation(project.sjyLibrary("androidx-activity-compose"))
        implementation(project.sjyLibrary("androidx-ui-tooling-preview"))
        implementation(project.sjyLibrary("androidx-ui-tooling"))
    }
    sourceSets.commonMain.dependencies {
        implementation(project.sjyLibrary("coil-network-ktor3"))
        implementation(project.sjyLibrary("compose-runtime"))
        implementation(project.sjyLibrary("compose-foundation"))
        implementation(project.sjyLibrary("compose-material3-multiplatform"))
        implementation(project.sjyLibrary("compose-ui-multiplatform"))
        implementation(project.sjyLibrary("compose-components-resources"))
        implementation(project.sjyLibrary("compose-components-ui-tooling-preview"))
        implementation(project.sjyLibrary("orbit-mvi-core"))
        implementation(project.sjyLibrary("orbit-mvi-viewmodel"))
        implementation(project.sjyLibrary("orbit-mvi-compose"))
        implementation(project.sjyLibrary("androidx-lifecycle-viewmodelCompose"))
        implementation(project.sjyLibrary("androidx-lifecycle-runtimeCompose"))
        implementation(project.sjyLibrary("navigation3-ui"))
        implementation(project.sjyLibrary("navigation3-adaptive"))
        implementation(project.sjyLibrary("navigation3-viewmodel"))
    }
}

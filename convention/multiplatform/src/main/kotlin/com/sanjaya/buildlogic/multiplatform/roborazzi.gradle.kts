package com.sanjaya.buildlogic.multiplatform

import com.sanjaya.buildlogic.sjyLibrary
import com.sanjaya.buildlogic.sjyPlugin

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

println("[Build Logic][RoborazziSetup] Setting up Roborazzi for project: ${project.name}")

pluginManager.apply(project.sjyPlugin("roborazzi"))

configure<KotlinMultiplatformExtension> {
    sourceSets.matching { it.name == "androidHostTest" }.configureEach {
        dependencies {
            implementation(project.sjyLibrary("junit"))
            implementation(project.sjyLibrary("robolectric"))
            implementation(project.sjyLibrary("roborazzi"))
            implementation(project.sjyLibrary("roborazzi-compose"))
            implementation(project.sjyLibrary("compose-ui-test-junit4"))
            implementation(project.sjyLibrary("androidx-ui-test-manifest"))
        }
    }
}

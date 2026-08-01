package com.sanjaya.buildlogic.multiplatform

import com.sanjaya.buildlogic.SyncIosAppVersionTask
import com.sanjaya.buildlogic.sjyVersion

import org.gradle.kotlin.dsl.register

println("[Build Logic][IosAppVersion] Registering syncIosAppVersion for project: ${project.name}")

val syncIosAppVersion = tasks.register<SyncIosAppVersionTask>("syncIosAppVersion") {
    group = "versioning"
    description = "Write iosApp/Configuration/Version.xcconfig from version catalog"
    versionName.set(project.sjyVersion("app-version-name"))
    versionCode.set(project.sjyVersion("app-version-code"))
    outputFile.set(layout.settingsDirectory.file("iosApp/Configuration/Version.xcconfig"))
}

tasks.configureEach {
    if (isIosFrameworkConsumerTask(name)) {
        dependsOn(syncIosAppVersion)
    }
}

fun isIosFrameworkConsumerTask(taskName: String): Boolean =
    taskName == "embedAndSignAppleFrameworkForXcode" ||
        (taskName.startsWith("link") && taskName.contains("FrameworkIos")) ||
        (taskName.startsWith("assemble") && taskName.contains("AppleFramework"))

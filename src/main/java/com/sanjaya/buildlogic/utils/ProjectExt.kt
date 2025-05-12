package com.sanjaya.buildlogic.utils

import org.gradle.api.Project

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

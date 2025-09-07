package com.sanjaya.buildlogic.android.utils

import org.gradle.api.Project
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class AndroidProjectTypeChecker(
    @InjectedParam private val project: Project
) {
    fun isApp(): Boolean {
        return project.pluginManager.hasPlugin("com.android.application")
    }

    fun isLib(): Boolean {
        return project.pluginManager.hasPlugin("com.android.library")
    }

    fun isAppOrLib(): Boolean {
        val isApp = isApp()
        val isLib = isLib()
        return isApp || isLib
    }
}

package com.sanjaya.buildlogic.common.plugins

import com.sanjaya.buildlogic.di.BuildLogicModule
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

open class BasePlugin : Plugin<Project>, KoinComponent {
    override fun apply(target: Project) {
        runCatching {
            startKoin {
                printLogger()
                modules(
                    listOf(BuildLogicModule.module)
                )
            }
        }
    }
}
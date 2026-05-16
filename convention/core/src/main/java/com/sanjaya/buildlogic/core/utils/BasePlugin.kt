package com.sanjaya.buildlogic.core.utils

import com.sanjaya.buildlogic.core.di.ConventionCoreModule
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import java.util.ServiceLoader

abstract class BasePlugin : Plugin<Project>, KoinComponent {
    override fun apply(target: Project) {
        runCatching {
            startKoin {
                printLogger()
                modules(ConventionCoreModule.module)
            }
        }
        val loader = ServiceLoader.load(KoinModuleInstaller::class.java)
        loader.forEach { it.install() }
        setup(target)
    }

    abstract fun setup(target: Project)
}

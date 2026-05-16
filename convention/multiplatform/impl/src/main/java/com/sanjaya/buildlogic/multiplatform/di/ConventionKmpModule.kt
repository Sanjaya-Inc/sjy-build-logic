package com.sanjaya.buildlogic.multiplatform.di

import com.google.auto.service.AutoService
import com.sanjaya.buildlogic.core.utils.KoinModuleInstaller
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.context.loadKoinModules
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.sanjaya.buildlogic.multiplatform")
object ConventionKmpModule {

    @AutoService(KoinModuleInstaller::class)
    class Installer : KoinModuleInstaller {
        override fun install() {
            loadKoinModules(ConventionKmpModule.module)
        }
    }
}

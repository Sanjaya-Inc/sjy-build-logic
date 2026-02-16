package com.sanjaya.buildlogic.multiplatform.components.setup

import com.google.devtools.ksp.gradle.KspExtension
import com.sanjaya.buildlogic.common.components.BuildLogicLogger
import com.sanjaya.buildlogic.common.components.DependenciesFinder
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent

@Factory
class KmpKoinSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val dependenciesFinder: DependenciesFinder = ComponentProvider.provide(project)
) : KoinComponent {

    fun setup() {
        logger.title(TAG, "Setting up Koin for kmp project: ${project.name}")
        project.configure<KotlinMultiplatformExtension> {
            sourceSets.commonMain.dependencies {
                val bom = dependenciesFinder.findLibrary("koin-bom")
                implementation(this.project.dependencies.platform(bom))
                val bundles = dependenciesFinder.findBundle("koin-nonandroid")
                implementation(bundles)
                val annotation = dependenciesFinder.findLibrary("koin-annotation")
                implementation(annotation)
            }
            sourceSets.androidMain.dependencies {
                val bom = dependenciesFinder.findLibrary("koin-bom")
                implementation(this.project.dependencies.platform(bom))
                val bundles = dependenciesFinder.findBundle("koin-android")
                implementation(bundles)
                val annotation = dependenciesFinder.findLibrary("koin-annotation")
                implementation(annotation)
            }
            sourceSets.named("commonMain").configure {
                kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            }
        }
        project.dependencies {
            val compiler = dependenciesFinder.findLibrary("koin-ksp")
            add("ksp", compiler)
        }
        project.tasks.matching { it.name.startsWith("ksp") && it.name != "kspCommonMainKotlinMetadata" }
            .configureEach {
                dependsOn("kspCommonMainKotlinMetadata")
            }
        project.the<KspExtension>().apply {
            arg("KOIN_CONFIG_CHECK", "false")
        }
    }

    companion object {
        private const val TAG = "AndroidKoinSetup"
    }
}
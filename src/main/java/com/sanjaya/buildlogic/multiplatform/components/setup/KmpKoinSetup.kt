package com.sanjaya.buildlogic.multiplatform.components.setup

import com.google.devtools.ksp.gradle.KspExtension
import com.sanjaya.buildlogic.android.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.common.components.BuildLogicLogger
import com.sanjaya.buildlogic.common.components.DependenciesFinder
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.internal.builtins.StandardNames.FqNames.target
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

@Factory
class KmpKoinSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger
) : KoinComponent {

    private val dependenciesFinder: DependenciesFinder by inject { parametersOf(project) }

    fun setup() {
        logger.title(TAG, "Setting up Koin for kmp project: ${project.name}")
        project.configure<KotlinMultiplatformExtension>() {
            sourceSets.commonMain.dependencies {
                val bom = dependenciesFinder.findLibrary("koin-bom")
                implementation(this.project.dependencies.platform(bom))
                val bundles = dependenciesFinder.findBundle("koin-nonandroid")
                implementation(bundles)
                val annotation = dependenciesFinder.findLibrary("koin-annotation")
                api(annotation)
            }
            sourceSets.androidMain.dependencies {
                val bom = dependenciesFinder.findLibrary("koin-bom")
                implementation(this.project.dependencies.platform(bom))
                val bundles = dependenciesFinder.findBundle("koin-android")
                implementation(bundles)
                val annotation = dependenciesFinder.findLibrary("koin-annotation")
                api(annotation)
            }
            sourceSets.named("commonMain").configure {
                kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            }
        }
        project.dependencies {
            val compiler = dependenciesFinder.findLibrary("koin-ksp")
            add("ksp", compiler)
        }
        project.tasks.matching { it.name.startsWith("ksp") && it.name != "kspCommonMainKotlinMetadata" }.configureEach {
            dependsOn("kspCommonMainKotlinMetadata")
        }
        project.the<KspExtension>().apply {
            arg("KOIN_CONFIG_CHECK", "true")
        }
    }

    companion object {
        private const val TAG = "AndroidKoinSetup"
    }
}
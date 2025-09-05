package com.sanjaya.buildlogic.multiplatform.components.setup

import com.sanjaya.buildlogic.android.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.common.components.BuildLogicLogger
import com.sanjaya.buildlogic.common.components.DependenciesFinder
import com.sanjaya.buildlogic.common.components.KspSetup
import com.sanjaya.buildlogic.common.components.PluginApplicator
import com.sanjaya.buildlogic.common.components.VersionFinder
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.internal.builtins.StandardNames.FqNames.target
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.getValue
import kotlin.jvm.java

@Factory
class KmpCommonSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger
) : KoinComponent {

    private val pluginApplicator: PluginApplicator by inject { parametersOf(project) }
    private val dependenciesFinder: DependenciesFinder by inject { parametersOf(project) }
    private val versionFinder: VersionFinder by inject { parametersOf(project) }
    private val kspSetup: KspSetup by inject { parametersOf(project) }
    private val kmpKoinSetup: KmpKoinSetup by inject { parametersOf(project) }

    fun setup() {
        pluginApplicator.applyPluginsByAliases(
            "kotlin-multiplatform"
        )
        pluginApplicator.applyPluginsByIds(
            "com.sanjaya.buildlogic.detekt",
            "com.sanjaya.buildlogic.target",
        )
        kspSetup.setup()
        kmpKoinSetup.setup()
        project.configure<KotlinMultiplatformExtension>() {
            sourceSets.commonTest {
                dependencies {
                    val kotlinTest = dependenciesFinder.findLibrary("kotlin-test")
                    implementation(kotlinTest)
                }
            }
            logger.i(TAG, "Configuring android target for ${project.name}")
            androidTarget {
                compilerOptions {
                    jvmTarget.set(
                        JvmTarget.fromTarget(
                            versionFinder.find("jvm-target").toString()
                        )
                    )
                }
            }
            logger.i(TAG, "Configuring ios target for ${project.name}")
            listOf(
                iosArm64(),
                iosSimulatorArm64()
            ).forEach { iosTarget ->
                iosTarget.binaries.framework {
                    baseName = project.name
                    isStatic = true

                    val bundleId = project.findProperty("ios.bundleId") as? String
                    if (bundleId != null) {
                        freeCompilerArgs += listOf("-Xbinary=bundleId=$bundleId")
                        logger.i(TAG, "Using bundleId=$bundleId for ${project.name}")
                    } else {
                        logger.i(TAG, "No ios.bundleId found for ${project.name}, using default")
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "KmpCommonSetup"
    }
}
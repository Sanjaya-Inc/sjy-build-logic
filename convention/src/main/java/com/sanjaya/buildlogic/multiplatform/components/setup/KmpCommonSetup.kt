package com.sanjaya.buildlogic.multiplatform.components.setup

import com.sanjaya.buildlogic.common.components.BuildLogicLogger
import com.sanjaya.buildlogic.common.components.DependenciesFinder
import com.sanjaya.buildlogic.common.components.KspSetup
import com.sanjaya.buildlogic.common.components.PluginApplicator
import com.sanjaya.buildlogic.common.components.VersionFinder
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent

@Factory
class KmpCommonSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val pluginApplicator: PluginApplicator = ComponentProvider.provide(project),
    private val dependenciesFinder: DependenciesFinder = ComponentProvider.provide(project),
    private val versionFinder: VersionFinder = ComponentProvider.provide(project),
    private val kspSetup: KspSetup = ComponentProvider.provide(project),
    private val kmpKoinSetup: KmpKoinSetup = ComponentProvider.provide(project),
    private val kmpKotlinSetup: KmpKotlinSetup = ComponentProvider.provide(project),
    private val kmpDataSetup: KmpDataSetup = ComponentProvider.provide(project)
) : KoinComponent {

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
        kmpKotlinSetup.setup()
        kmpDataSetup.setup()
        project.configure<KotlinMultiplatformExtension> {
            sourceSets.commonMain {
                dependencies {
                    listOf(
                        "napier"
                    ).map(dependenciesFinder::findLibrary).forEach(::implementation)
                }
            }
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
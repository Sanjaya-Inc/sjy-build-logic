package com.sanjaya.buildlogic.multiplatform

import com.android.build.api.variant.KotlinMultiplatformAndroidComponentsExtension
import com.sanjaya.buildlogic.core.utils.BuildLogicLogger
import com.sanjaya.buildlogic.core.utils.DependenciesFinder
import com.sanjaya.buildlogic.core.utils.KspSetup
import com.sanjaya.buildlogic.core.utils.PluginApplicator
import com.sanjaya.buildlogic.core.utils.VersionFinder
import com.sanjaya.buildlogic.core.utils.get
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class KmpCommonSetupImpl(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val pluginApplicator: PluginApplicator = project.get(),
    private val dependenciesFinder: DependenciesFinder = project.get(),
    private val versionFinder: VersionFinder = project.get(),
    private val kspSetup: KspSetup = project.get(),
    private val kmpKoinSetup: KmpKoinSetup = project.get(),
    private val kmpKotlinSetup: KmpKotlinSetup = project.get(),
    private val kmpDataSetup: KmpDataSetup = project.get()
) : KmpCommonSetup {

    override fun setup() {
        pluginApplicator.applyPluginsByAliases(
            "kotlin-multiplatform",
            "android-kotlin-multiplatform-library"
        )
        kspSetup.setup()
        kmpKoinSetup.setup()
        kmpKotlinSetup.setup()
        kmpDataSetup.setup()
        project.the<KotlinMultiplatformAndroidComponentsExtension>().apply {
            this.finalizeDsl {
                it.androidResources.enable = true
                it.minSdk = versionFinder.find("min-sdk").toString().toInt()
                it.compileSdk = versionFinder.find("compile-sdk").toString().toInt()
            }
        }
        project.the<KotlinMultiplatformExtension>().apply {
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
            logger.i(TAG, "Configuring ios target for ${project.name}")
            listOf(
                iosArm64(),
                iosSimulatorArm64()
            ).forEach { iosTarget ->
                iosTarget.binaries.framework {
                    baseName = project.name.toPascalCase()
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

    /**
     * Converts a Gradle project name to PascalCase for XCFramework naming.
     * Handles standard Gradle naming conventions: kebab-case and snake_case.
     *
     * Examples:
     * - "compose-app" -> "ComposeApp"
     * - "my_project" -> "MyProject"
     * - "composeApp" -> "ComposeApp"
     */
    private fun String.toPascalCase(): String {
        return split('-', '_')
            .joinToString("") { word ->
                word.replaceFirstChar { it.uppercaseChar() }
            }
    }

    companion object {
        private const val TAG = "KmpCommonSetup"
    }
}

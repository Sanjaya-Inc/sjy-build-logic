package com.sanjaya.buildlogic.components.setup

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.sanjaya.buildlogic.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.components.misc.BuildLogicLogger
import com.sanjaya.buildlogic.components.misc.VersionFinder
import com.sanjaya.buildlogic.utils.isApp
import com.sanjaya.buildlogic.utils.isAppOrLib
import com.sanjaya.buildlogic.utils.isLib
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.the
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

/**
 * Sets up the Android target configuration for the given project.
 *
 * This class configures the Android target, including:
 * - Determining the project type (application or library)
 * - Setting the target and minimum SDK versions
 * - Configuring the Java toolchain
 * - Adding core dependencies
 *
 * @property project The Gradle project to set up the Android target for.
 * @property logger The logger to use for logging.
 */
@Factory
class AndroidTargetSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger
) : KoinComponent {

    private val versionFinder: VersionFinder by inject { parametersOf(project) }
    private val dependenciesApplicator: AndroidDependenciesApplicator by inject {
        parametersOf(
            project
        )
    }

    fun setup() {
        logger.title(TAG, "Setting up Android Target for project: ${project.name}")
        with(project) {
            if (!isAppOrLib()) return@with
            val type =
                when {
                    isApp() -> {
                        the<ApplicationExtension>().apply {
                            defaultConfig {
                                multiDexEnabled = true
                                this@AndroidTargetSetup.logger.i(TAG, "Set Multidex enabled")
                            }
                        }
                        ApplicationExtension::class
                    }

                    isLib() -> LibraryExtension::class
                    else -> null
                } ?: return@with

            the(type).apply {
                val targetSdk = versionFinder.find("compile-sdk").toString().toInt()
                val minSdkVersion = versionFinder.find("min-sdk").toString().toInt()
                if (this is ApplicationExtension) {
                    defaultConfig.targetSdk = targetSdk
                }
                compileSdk = versionFinder.find("compile-sdk").toString().toInt()
                defaultConfig {
                    minSdk = minSdkVersion
                }

                buildFeatures {
                    buildConfig = false
                }

                this@AndroidTargetSetup.logger.i(
                    TAG,
                    "Setting up target android sdk to: $targetSdk"
                )
                this@AndroidTargetSetup.logger.i(
                    TAG,
                    "Setting up min android sdk to: $minSdkVersion"
                )
                the<JavaPluginExtension>().toolchain {
                    languageVersion.set(
                        JavaLanguageVersion.of(
                            versionFinder.find("jvm-target").toString()
                        )
                    )
                }
            }
        }
        dependenciesApplicator.implementations("androidx-core-ktx")
    }

    companion object {
        const val TAG = "AndroidTargetSetup"
    }
}

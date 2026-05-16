package com.sanjaya.buildlogic.android

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.sanjaya.buildlogic.android.utils.AndroidProjectTypeChecker
import com.sanjaya.buildlogic.core.android.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.core.utils.BuildLogicLogger
import com.sanjaya.buildlogic.core.utils.VersionFinder
import com.sanjaya.buildlogic.core.utils.get
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.the
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

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
class AndroidTargetSetupImpl(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val versionFinder: VersionFinder = project.get(),
    private val dependenciesApplicator: AndroidDependenciesApplicator = project.get(),
    private val projectTypeChecker: AndroidProjectTypeChecker = project.get(),
) : AndroidTargetSetup {

    override fun setup() = with(project) {
        this@AndroidTargetSetupImpl.logger.title(
            TAG,
            "Setting up Android Target for project: ${project.name}"
        )
        if (!projectTypeChecker.isAppOrLib()) return@with
        
        val targetSdkVersion = versionFinder.find("compile-sdk").toString().toInt()
        val minSdkVersion = versionFinder.find("min-sdk").toString().toInt()
        
        when {
            projectTypeChecker.isApp() -> {
                the<ApplicationExtension>().apply {
                    compileSdk = targetSdkVersion
                    defaultConfig {
                        multiDexEnabled = true
                        targetSdk = targetSdkVersion
                        minSdk = minSdkVersion
                        this@AndroidTargetSetupImpl.logger.i(TAG, "Set Multidex enabled")
                    }
                    buildFeatures {
                        buildConfig = false
                    }
                }
            }
            projectTypeChecker.isLib() -> {
                the<LibraryExtension>().apply {
                    compileSdk = targetSdkVersion
                    defaultConfig {
                        minSdk = minSdkVersion
                    }
                    buildFeatures {
                        buildConfig = false
                    }
                }
            }
            else -> return@with
        }

        this@AndroidTargetSetupImpl.logger.i(
            TAG,
            "Setting up target android sdk to: $targetSdkVersion"
        )
        this@AndroidTargetSetupImpl.logger.i(
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
        dependenciesApplicator.implementations("androidx-core-ktx")
    }

    companion object {
        const val TAG = "AndroidTargetSetup"
    }
}

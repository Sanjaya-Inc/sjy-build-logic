package com.sanjaya.buildlogic.common.components

import com.sanjaya.buildlogic.android.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent

/**
 * Sets up Detekt for the given project.
 *
 * Detekt is a static code analysis tool for Kotlin. This class configures Detekt
 * with sensible defaults, including:
 * - Applying the Detekt plugin
 * - Configuring the Detekt extension
 * - Configuring the Detekt tasks
 * - Adding Detekt dependencies
 * - Attaching the Detekt task to the build process
 *
 * @property project The Gradle project to set up Detekt for.
 * @property buildLogicLogger The logger to use for logging.
 */
@Factory
class DetektSetup(
    @InjectedParam private val project: Project,
    private val buildLogicLogger: BuildLogicLogger,
    private val versionFinder: VersionFinder = ComponentProvider.provide(project),
    private val dependenciesApplicator: AndroidDependenciesApplicator = ComponentProvider.provide(
        project
    ),
    private val pluginApplicator: PluginApplicator = ComponentProvider.provide(project)
) : KoinComponent {

    fun setup() {
        buildLogicLogger.title(TAG, "Setting up Detekt for project: ${project.name}")
        project.applyDetekt()
    }

    private fun Project.applyDetekt() {
        runCatching {
            applyDetektPlugin()
        }.onSuccess {
            configureDetekt()
            configureDetektTasks()
            addDetektDependencies()
            attachDetektTask()
        }.onFailure {
            buildLogicLogger.i(TAG, "Failed to apply detekt plugin: ${it.message}")
        }
    }

    private fun applyDetektPlugin() {
        pluginApplicator.applyPluginsByAliases("detekt")
            .also {
                buildLogicLogger.i(
                    TAG,
                    "Success applying detekt plugin, starting configuration.."
                )
            }
    }

    private fun Project.configureDetekt() {
        the<DetektExtension>().apply {
            this@configureDetekt.configureDetektExtension(this)
        }
    }

    private fun Project.configureDetektExtension(extension: DetektExtension) {
        extension.apply {
            buildUponDefaultConfig = true
            allRules = false
            autoCorrect = true
            config.setFrom(determineDetektConfig())
            baseline = file("../config/detekt-${project.name}-baseline.xml")
            parallel = true
        }
    }

    private fun Project.determineDetektConfig() =
        if (rootProject.file("config/detekt-rule.yml").exists()) {
            rootProject.file("config/detekt-rule.yml")
                .also { buildLogicLogger.i(TAG, "Using external detekt config file: $it") }
        } else {
            rootProject.file("sjy-build-logic/config/detekt-rule.yml")
                .also { buildLogicLogger.i(TAG, "Using default detekt config file: $it") }
        }

    private fun Project.configureDetektTasks() {
        val jvmTarget = versionFinder.find("jvm-target").toString()
        tasks.withType<Detekt>().configureEach {
            configureDetektTask(this, jvmTarget)
        }
        tasks.withType<DetektCreateBaselineTask>().configureEach {
            this.jvmTarget = jvmTarget
        }
    }

    private fun Project.configureDetektTask(task: Detekt, jvmTarget: String) {
        task.apply {
            this.jvmTarget = jvmTarget
            setSource(
                files(
                    "src/main/java",
                    "src/main/kotlin",
                    "src/test/java",
                    "src/test/kotlin",
                    "src/androidTest/java",
                    "src/androidTest/kotlin",
                    "src/androidDeviceTest/java",
                    "src/androidDeviceTest/kotlin",
                    "src/androidHostTest/java",
                    "src/androidHostTest/kotlin",
                    "src/commonMain/java",
                    "src/commonMain/kotlin",
                    "src/commonTest/java",
                    "src/commonTest/kotlin",
                    "src/androidMain/java",
                    "src/androidMain/kotlin",
                    "src/iosMain/java",
                    "src/iosMain/kotlin",
                    "src/jvmMain/java",
                    "src/jvmMain/kotlin"
                )
            )
            exclude("**/build/**")
            reports {
                html.required.set(true)
                xml.required.set(true)
                txt.required.set(true)
                sarif.required.set(true)
                md.required.set(true)
            }
        }
    }

    private fun addDetektDependencies() {
        dependenciesApplicator.detektPlugins(
            "detekt-formatting",
            "detekt-twitter"
        )
    }

    private fun Project.attachDetektTask() {
        runCatching {
            tasks.whenTaskAdded {
                if (name.startsWith("assemble") || name.startsWith("compile") || name == "run") {
                    dependsOn(tasks.getByName("detekt"))
                }
            }
        }.onFailure {
            buildLogicLogger.i(TAG, "Failed to attach detekt task: ${it.message}")
        }
    }

    companion object {
        const val TAG = "DetektSetup"
    }
}
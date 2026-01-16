package com.sanjaya.buildlogic.android.components.setup

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.sanjaya.buildlogic.android.components.dependency.AndroidDependenciesApplicator
import com.sanjaya.buildlogic.android.utils.AndroidProjectTypeChecker
import com.sanjaya.buildlogic.common.components.BuildLogicLogger
import com.sanjaya.buildlogic.common.components.VersionFinder
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent

@Factory
class TestSetup(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val dependenciesApplicator: AndroidDependenciesApplicator = ComponentProvider.provide(
        project
    ),
    private val projectTypeChecker: AndroidProjectTypeChecker = ComponentProvider.provide(project),
    private val versionFinder: VersionFinder = ComponentProvider.provide(project),
) : KoinComponent {

    fun setup() {
        logger.title(TAG, "Setting up test framework for project: ${project.name}")
        applyTestDependencies()
        configureAndroidTestOptions()
        configureJacoco()
        setupJacocoReports()
    }

    private fun applyTestDependencies() {
        val testDependencies = arrayOf(
            "junit",
            "junit-vintage-engine",
            "junit-jupiter",
            "junit-jupiter-api",
            "junit-jupiter-engine",
            "kotlin-test",
            "mockk",
            "mockk-android",
            "mockk-agent",
            "turbine",
            "coroutines-test"
        )
        dependenciesApplicator.testImplementations(*testDependencies)
        dependenciesApplicator.androidTestImplementations(*testDependencies)
    }

    private fun configureAndroidTestOptions() {
        if (!projectTypeChecker.isAppOrLib()) return

        when {
            projectTypeChecker.isApp() -> {
                project.the<ApplicationExtension>().apply {
                    testOptions {
                        unitTests.all { it.useJUnitPlatform() }
                    }
                }
            }
            projectTypeChecker.isLib() -> {
                project.the<LibraryExtension>().apply {
                    testOptions {
                        unitTests.all { it.useJUnitPlatform() }
                    }
                }
            }
        }
    }

    private fun configureJacoco() {
        if (!projectTypeChecker.isAppOrLib()) return

        project.pluginManager.apply("jacoco")
        project.extensions.configure<JacocoPluginExtension> {
            toolVersion = versionFinder.find("jacoco").toString()
        }
        project.tasks.withType<Test>().configureEach {
            if (name.startsWith("test") && name.endsWith("UnitTest")) {
                extensions.configure(JacocoTaskExtension::class) {
                    isIncludeNoLocationClasses = true
                    excludes = listOf("jdk.internal.*")
                }
            }
        }
    }

    private fun setupJacocoReports() {
        if (!projectTypeChecker.isAppOrLib()) return

        when {
            projectTypeChecker.isApp() -> {
                val componentsExtension = project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
                componentsExtension.onVariants { variant ->
                    if (variant.buildType == "debug") {
                        val variantName = variant.name.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase() else it.toString()
                        }
                        val testTask = project.tasks.named("test${variantName}UnitTest")
                        registerJacocoReportTask(variantName, testTask, variant.name)
                    }
                }
            }
            projectTypeChecker.isLib() -> {
                val componentsExtension = project.extensions.getByType(LibraryAndroidComponentsExtension::class.java)
                componentsExtension.onVariants { variant ->
                    if (variant.buildType == "debug") {
                        val variantName = variant.name.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase() else it.toString()
                        }
                        val testTask = project.tasks.named("test${variantName}UnitTest")
                        registerJacocoReportTask(variantName, testTask, variant.name)
                    }
                }
            }
        }
    }

    private fun registerJacocoReportTask(
        variantName: String,
        testTask: TaskProvider<Task>,
        variantNameLower: String
    ) {
        project.tasks.register<JacocoReport>("jacoco${variantName}Report") {
            group = "verification"
            dependsOn(testTask)
            classDirectories.setFrom(
                project.layout.buildDirectory.dir("tmp/kotlin-classes/$variantNameLower")
                    .get().asFileTree.matching { exclude(JACOCO_FILE_FILTER) },
                project.layout.buildDirectory.dir("intermediates/javac/$variantNameLower/classes")
                    .get().asFileTree.matching { exclude(JACOCO_FILE_FILTER) }
            )
            val sources = listOf(
                project.layout.projectDirectory.file("src/main/java"),
                project.layout.projectDirectory.file("src/main/kotlin")
            )
            sourceDirectories.setFrom(sources)
            executionData.setFrom(
                project.layout.buildDirectory.dir("jacoco").get()
                    .asFileTree.matching { include("**/test${variantName}UnitTest.exec") }
            )
            reports {
                xml.required.set(true)
                html.required.set(true)
            }
        }
    }

    private companion object {
        const val TAG = "TestSetup"
        private val JACOCO_FILE_FILTER = listOf(
            // Dagger / Hilt generated
            "**/*_MembersInjector.class",
            "**/*_Factory.class",
            "**/*Module.*",
            "**/*_HiltModules_*",
            "**/*Hilt_*",
            "**/hilt_aggregated_deps/**",
            "**/dagger/**",
            "**/di/**",

            // AssistedInject / Anvil
            "**/*_AssistedFactory.class",
            "**/*_AssistedInject*.class",
            "**/*_GeneratedInjector.class",

            // Android generated
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",

            // AndroidX / Lifecycle
            "**/*_ViewBinding*",
            "**/*_ViewModel*",
            "**/*_ViewModelFactory*",
            "**/*_Impl*",

            // Data binding
            "**/databinding/**/*",
            "**/BR.class",

            // Compose
            "**/*ComposableSingletons*",
            "**/*PreviewParameter*",

            // Other generated (kapt, etc.)
            "**/*MapperImpl*",
            "**/*ModuleDeps*",
            "**/*KtLambda*",

            // Koin generated
            "**/*Definition*",
            "**/*Koin*",
            "**/*Module*",
            "**/_KSP_**",
        )
    }
}

package com.sanjaya.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

val jacocoFileFilter = listOf(
    "**/*_MembersInjector.class",
    "**/*_Factory.class",
    "**/*Module.*",
    "**/*_HiltModules_*",
    "**/*Hilt_*",
    "**/hilt_aggregated_deps/**",
    "**/dagger/**",
    "**/di/**",
    "**/*_AssistedFactory.class",
    "**/*_AssistedInject*.class",
    "**/*_GeneratedInjector.class",
    "**/R.class",
    "**/R\$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*_ViewBinding*",
    "**/*_ViewModel*",
    "**/*_ViewModelFactory*",
    "**/*_Impl*",
    "**/databinding/**/*",
    "**/BR.class",
    "**/*ComposableSingletons*",
    "**/*PreviewParameter*",
    "**/*MapperImpl*",
    "**/*ModuleDeps*",
    "**/*KtLambda*",
    "**/*Definition*",
    "**/*Koin*",
    "**/*Module*",
    "**/_KSP_**"
)

println("[Build Logic][TestSetup] Setting up test framework for project: ${project.name}")

val testDependencies = listOf(
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

dependencies {
    testDependencies.forEach { alias ->
        add("testImplementation", project.sjyLibrary(alias))
        add("androidTestImplementation", project.sjyLibrary(alias))
    }
}

val isApp = pluginManager.hasPlugin("com.android.application")
val isLib = pluginManager.hasPlugin("com.android.library")

if (isApp || isLib) {
    if (isApp) {
        configure<ApplicationExtension> {
            testOptions {
                unitTests.all { it.useJUnitPlatform() }
            }
        }
    } else {
        configure<LibraryExtension> {
            testOptions {
                unitTests.all { it.useJUnitPlatform() }
            }
        }
    }

    pluginManager.apply("jacoco")
    configure<JacocoPluginExtension> {
        toolVersion = project.sjyVersion("jacoco")
    }

    tasks.withType<Test>().configureEach {
        if (name.startsWith("test") && name.endsWith("UnitTest")) {
            extensions.configure<JacocoTaskExtension> {
                isIncludeNoLocationClasses = true
                excludes = listOf("jdk.internal.*")
            }
        }
    }

    fun registerJacocoReportTask(variantName: String, testTaskName: String, variantNameLower: String) {
        val testTask = tasks.named(testTaskName)
        tasks.register<JacocoReport>("jacoco${variantName}Report") {
            group = "verification"
            dependsOn(testTask)
            classDirectories.setFrom(
                layout.buildDirectory.dir("tmp/kotlin-classes/$variantNameLower")
                    .get().asFileTree.matching { exclude(jacocoFileFilter) },
                layout.buildDirectory.dir("intermediates/javac/$variantNameLower/classes")
                    .get().asFileTree.matching { exclude(jacocoFileFilter) }
            )
            sourceDirectories.setFrom(
                layout.projectDirectory.file("src/main/java"),
                layout.projectDirectory.file("src/main/kotlin")
            )
            executionData.setFrom(
                layout.buildDirectory.dir("jacoco").get()
                    .asFileTree.matching { include("**/$testTaskName.exec") }
            )
            reports {
                xml.required.set(true)
                html.required.set(true)
            }
        }
    }

    if (isApp) {
        extensions.getByType(ApplicationAndroidComponentsExtension::class.java).onVariants { variant ->
            if (variant.buildType == "debug") {
                val variantName = variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                registerJacocoReportTask(variantName, "test${variantName}UnitTest", variant.name)
            }
        }
    } else {
        extensions.getByType(LibraryAndroidComponentsExtension::class.java).onVariants { variant ->
            if (variant.buildType == "debug") {
                val variantName = variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                registerJacocoReportTask(variantName, "test${variantName}UnitTest", variant.name)
            }
        }
    }
}

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
    "junit-platform-launcher",
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

pluginManager.withPlugin("com.android.application") {
    configureAndroidUnitTests {
        configure<ApplicationExtension> {
            testOptions {
                unitTests.all { it.useJUnitPlatform() }
            }
        }
    }
    configure<ApplicationAndroidComponentsExtension> {
        onVariants { variant ->
            if (variant.buildType != "debug") return@onVariants
            registerJacocoReportTask(variant.name)
        }
    }
}

pluginManager.withPlugin("com.android.library") {
    configureAndroidUnitTests {
        configure<LibraryExtension> {
            testOptions {
                unitTests.all { it.useJUnitPlatform() }
            }
        }
    }
    configure<LibraryAndroidComponentsExtension> {
        onVariants { variant ->
            if (variant.buildType != "debug") return@onVariants
            registerJacocoReportTask(variant.name)
        }
    }
}

fun Project.configureAndroidUnitTests(configureExtension: Project.() -> Unit) {
    configureExtension()
    pluginManager.apply("jacoco")
    configure<JacocoPluginExtension> {
        toolVersion = sjyVersion("jacoco")
    }
    tasks.withType<Test>().configureEach {
        if (!name.startsWith("test") || !name.endsWith("UnitTest")) return@configureEach
        extensions.configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }
}

fun Project.registerJacocoReportTask(variantNameLower: String) {
    val variantName = variantNameLower.replaceFirstChar { character ->
        if (character.isLowerCase()) character.titlecase() else character.toString()
    }
    val testTaskName = "test${variantName}UnitTest"
    val kotlinClasses = filteredClassTree("tmp/kotlin-classes/$variantNameLower")
    val javaClasses = filteredClassTree("intermediates/javac/$variantNameLower/classes")
    val report = tasks.register<JacocoReport>("jacoco${variantName}Report") {
        group = "verification"
        description = "JaCoCo coverage report for $variantName"
        classDirectories.setFrom(kotlinClasses, javaClasses)
        sourceDirectories.setFrom(
            project.layout.projectDirectory.file("src/main/java"),
            project.layout.projectDirectory.file("src/main/kotlin"),
        )
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
    tasks.withType<Test>().configureEach {
        if (name != testTaskName) return@configureEach
        val coverageExec = layout.buildDirectory.file("jacoco/$name.exec")
        extensions.configure<JacocoTaskExtension> {
            setDestinationFile(coverageExec.map { it.asFile })
        }
        outputs.file(coverageExec)
        report.configure {
            executionData.setFrom(coverageExec)
        }
    }
}

fun Project.filteredClassTree(relativePath: String) =
    layout.buildDirectory.dir(relativePath).map { directory ->
        directory.asFileTree.matching { exclude(jacocoFileFilter) }
    }

package com.sanjaya.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.google.devtools.ksp.gradle.KspExtension

println("[Build Logic][AndroidKotlinSetup] Setting up Android Compose for project: ${project.name}")

pluginManager.withPlugin("com.android.application") {
    enableAndroidCompose {
        configure<ApplicationExtension> {
            buildFeatures {
                compose = true
            }
        }
    }
}

pluginManager.withPlugin("com.android.library") {
    enableAndroidCompose {
        configure<LibraryExtension> {
            buildFeatures {
                compose = true
            }
        }
    }
}

fun Project.enableAndroidCompose(configureExtension: Project.() -> Unit) {
    pluginManager.apply(sjyPlugin("kotlin-compose"))
    configureExtension()
    configureComposeCompilerReports()
    configure<KspExtension> {
        arg("compose-destinations.moduleName", project.name)
        arg("compose-destinations.htmlMermaidGraph", "$rootDir/navigation-docs")
        arg("compose-destinations.mermaidGraph", "$rootDir/navigation-docs")
    }
    addComposeDependencies()
}

fun Project.configureComposeCompilerReports() {
    if (providers.gradleProperty("composeCompilerReports").orNull != "true") return
    val reportsDir = layout.buildDirectory.dir("compose_compiler")
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions.freeCompilerArgs.addAll(
            reportsDir.map { directory ->
                val path = directory.asFile.absolutePath
                listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$path",
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$path",
                )
            },
        )
    }
}

fun Project.addComposeDependencies() {
    dependencies {
        add("implementation", sjyLibrary("androidx-activity-compose"))
        add("implementation", sjyLibrary("androidx-ui"))
        add("implementation", sjyLibrary("androidx-ui-graphics"))
        add("implementation", sjyLibrary("androidx-ui-tooling-preview"))
        add("implementation", sjyLibrary("androidx-material3"))
        add("implementation", sjyLibrary("compose-google-fonts"))
        add("implementation", sjyLibrary("coil"))
        add("implementation", sjyLibrary("coil-okhttp"))
        add("implementation", sjyLibrary("compose-destination-core"))
        add("implementation", sjyLibrary("compose-destination-sheet"))
        add("implementation", sjyLibrary("compose-icons"))
        add("ksp", sjyLibrary("compose-destination-ksp"))
        add("debugImplementation", sjyLibrary("androidx-ui-tooling"))
        add("debugImplementation", sjyLibrary("androidx-ui-test-manifest"))
    }
}

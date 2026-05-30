package com.sanjaya.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.google.devtools.ksp.gradle.KspExtension

println("[Build Logic][AndroidKotlinSetup] Setting up Android Compose for project: ${project.name}")

val isApp = pluginManager.hasPlugin("com.android.application")
val isLib = pluginManager.hasPlugin("com.android.library")

if (isApp || isLib) {
    pluginManager.apply(project.sjyPlugin("kotlin-compose"))

    if (isApp) {
        configure<ApplicationExtension> {
            buildFeatures {
                compose = true
            }
        }
    } else {
        configure<LibraryExtension> {
            buildFeatures {
                compose = true
            }
        }
    }

    configure<KspExtension> {
        arg("compose-destinations.moduleName", project.name)
        arg("compose-destinations.htmlMermaidGraph", "$rootDir/navigation-docs")
        arg("compose-destinations.mermaidGraph", "$rootDir/navigation-docs")
    }

    dependencies {
        add("implementation", project.sjyLibrary("androidx-activity-compose"))
        add("implementation", project.sjyLibrary("androidx-ui"))
        add("implementation", project.sjyLibrary("androidx-ui-graphics"))
        add("implementation", project.sjyLibrary("androidx-ui-tooling-preview"))
        add("implementation", project.sjyLibrary("androidx-material3"))
        add("implementation", project.sjyLibrary("compose-google-fonts"))
        add("implementation", project.sjyLibrary("coil"))
        add("implementation", project.sjyLibrary("coil-okhttp"))
        add("implementation", project.sjyLibrary("compose-destination-core"))
        add("implementation", project.sjyLibrary("compose-destination-sheet"))
        add("implementation", project.sjyLibrary("compose-icons"))

        add("ksp", project.sjyLibrary("compose-destination-ksp"))

        add("debugImplementation", project.sjyLibrary("androidx-ui-tooling"))
        add("debugImplementation", project.sjyLibrary("androidx-ui-test-manifest"))
    }
}

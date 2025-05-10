package com.sanjaya.buildlogic.utils

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

fun Project.configureFlavors() {
    when {
        isApp() -> the<ApplicationExtension>().apply {
            flavorDimensions += "environment"
            productFlavors {
                create("canary") {
                    dimension = "environment"
                }
                create("dev") {
                    dimension = "environment"
                }
                create("staging") {
                    dimension = "environment"
                }
                create("production") {
                    dimension = "environment"
                }
            }
        }

        isLib() -> the<LibraryExtension>().apply {
            flavorDimensions += "environment"
            productFlavors {
                create("canary") {
                    dimension = "environment"
                }
                create("dev") {
                    dimension = "environment"
                }
                create("staging") {
                    dimension = "environment"
                }
                create("production") {
                    dimension = "environment"
                }
            }
        }
    }
}

package com.sanjaya.buildlogic

import com.sanjaya.buildlogic.utils.configureKotlinMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        val kotlinMultiplatformExtension =
            extensions.findByType(KotlinMultiplatformExtension::class.java)
        requireNotNull(kotlinMultiplatformExtension) { "The 'kotlin-multiplatform' plugin was not applied to the project." }
        configureKotlinMultiplatform(kotlinMultiplatformExtension)
    }
}

package com.sanjaya.buildlogic

import com.sanjaya.buildlogic.utils.findLibs
import com.sanjaya.buildlogic.utils.findPlugin
import com.sanjaya.buildlogic.utils.implementationWithLog
import com.sanjaya.buildlogic.utils.conventions
import com.sanjaya.buildlogic.utils.printMessage
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.include
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType

class DetektConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val detekt = findPlugin("detekt", true) ?: return
            runCatching {
                pluginManager.apply(detekt)
            }.onSuccess {
                printMessage("Success applying detekt plugin, starting configuration..")
                the<DetektExtension>().apply {
                    buildUponDefaultConfig = true
                    allRules = false
                    autoCorrect = true
                    val externalConfig = file("../config/detekt-rule.yml")
                    val defaultConfig = file("config/detekt-rule.yml")

                    config.setFrom(if (externalConfig.exists()) {
                        printMessage("Using external detekt config file: $externalConfig")
                        externalConfig
                    } else {
                        printMessage("Using default detekt config file: $defaultConfig")
                        defaultConfig
                    })

                    baseline = file("../config/detekt-${name}-baseline.xml")
                    parallel = true
                }
                val jvmTarget = conventions.findVersion("jvm-target").get().toString()
                tasks.withType<Detekt>().configureEach {
                    this.jvmTarget = jvmTarget
                    reports {
                        html.required.set(true)
                        xml.required.set(true)
                        txt.required.set(true)
                        sarif.required.set(true)
                        md.required.set(true)
                    }
                }
                tasks.withType<DetektCreateBaselineTask>().configureEach {
                    this.jvmTarget = jvmTarget
                }

                dependencies {
                    findLibs("detekt-formatting")?.let {
                        add("detektPlugins", it)
                    }
                    findLibs("detekt-twitter")?.let {
                        add("detektPlugins", it)
                    }
                    findLibs("detekt-vkompose")?.let {
                        add("detektPlugins", it)
                    }
                }

                runCatching {
                    tasks.whenTaskAdded {
                        if (name.startsWith("assemble") || name.startsWith("compile") || name == "run") {
                            dependsOn(tasks.getByName("detekt"))
                        }
                    }
                }
            }
        }
    }
}

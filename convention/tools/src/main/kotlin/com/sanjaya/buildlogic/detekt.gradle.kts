package com.sanjaya.buildlogic

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.kotlin.dsl.withType

println("[Build Logic][DetektSetup] Setting up Detekt for project: ${project.name}")

val detektApplied = runCatching {
    pluginManager.apply(project.sjyPlugin("detekt"))
    println("[Build Logic][DetektSetup] Success applying detekt plugin, starting configuration..")
    true
}.getOrElse {
    println("[Build Logic][DetektSetup] Failed to apply detekt plugin: ${it.message}")
    false
}

if (detektApplied) {
    val detektConfigFile = if (rootProject.file("config/detekt-rule.yml").exists()) {
        rootProject.file("config/detekt-rule.yml").also {
            println("[Build Logic][DetektSetup] Using external detekt config file: $it")
        }
    } else {
        rootProject.file("sjy-build-logic/config/detekt-rule.yml").also {
            println("[Build Logic][DetektSetup] Using default detekt config file: $it")
        }
    }

    configure<DetektExtension> {
        buildUponDefaultConfig = true
        allRules = false
        autoCorrect = true
        parallel = true
        baseline = file("../config/detekt-${project.name}-baseline.xml")
        config.setFrom(detektConfigFile)
    }

    val jvmTargetVersion = project.sjyVersion("jvm-target")

    tasks.withType<Detekt>().configureEach {
        jvmTarget = jvmTargetVersion
        setSource(
            files(
                "src/main/java", "src/main/kotlin",
                "src/test/java", "src/test/kotlin",
                "src/androidTest/java", "src/androidTest/kotlin",
                "src/androidDeviceTest/java", "src/androidDeviceTest/kotlin",
                "src/androidHostTest/java", "src/androidHostTest/kotlin",
                "src/commonMain/java", "src/commonMain/kotlin",
                "src/commonTest/java", "src/commonTest/kotlin",
                "src/androidMain/java", "src/androidMain/kotlin",
                "src/iosMain/java", "src/iosMain/kotlin",
                "src/jvmMain/java", "src/jvmMain/kotlin"
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

    tasks.withType<DetektCreateBaselineTask>().configureEach {
        jvmTarget = jvmTargetVersion
    }

    dependencies {
        add("detektPlugins", project.sjyLibrary("detekt-formatting"))
        add("detektPlugins", project.sjyLibrary("detekt-compose"))
    }
}

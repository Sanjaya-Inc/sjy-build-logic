package com.sanjaya.buildlogic.utils

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.applyDebuggingTools() {
    dependencies {
        findLibs("chucker")?.let {
            add("canaryImplementation", it)
            add("devImplementation", it)
        }
        findLibs("chucker-no-op")?.let {
            add("stagingImplementation", it)
            add("productionImplementation", it)
        }
        findLibs("pluto")?.let {
            add("canaryImplementation", it)
            add("devImplementation", it)
        }
        findLibs("pluto-plugins")?.let {
            add("canaryImplementation", it)
            add("devImplementation", it)
        }
        findLibs("pluto-noop")?.let {
            add("stagingImplementation", it)
            add("productionImplementation", it)
        }
        findLibs("pluto-plugins-noop")?.let {
            add("stagingImplementation", it)
            add("productionImplementation", it)
        }
    }
}

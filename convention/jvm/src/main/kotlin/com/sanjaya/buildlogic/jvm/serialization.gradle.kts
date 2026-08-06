package com.sanjaya.buildlogic.jvm

import com.sanjaya.buildlogic.sjyLibrary

plugins {
    id("com.sanjaya.buildlogic.jvm.lib")
}

apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

dependencies {
    add("implementation", project.sjyLibrary("kotlinx-serialization-json"))
}

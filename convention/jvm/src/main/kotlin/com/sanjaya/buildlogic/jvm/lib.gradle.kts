package com.sanjaya.buildlogic.jvm

import com.sanjaya.buildlogic.sjyVersion

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.sanjaya.buildlogic.detekt")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(project.sjyVersion("jvm-target").toInt()))
    }
}

package com.sanjaya.buildlogic.jvm

import com.sanjaya.buildlogic.sjyLibrary

plugins {
    id("com.sanjaya.buildlogic.jvm.koin")
    application
}

dependencies {
    add("implementation", project.sjyLibrary("mcp-sdk"))
    add("implementation", project.sjyLibrary("logback"))
    add("implementation", project.sjyLibrary("coroutines-core"))
}

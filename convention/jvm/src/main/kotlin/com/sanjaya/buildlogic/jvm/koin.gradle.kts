package com.sanjaya.buildlogic.jvm

import com.sanjaya.buildlogic.sjyLibrary
import com.sanjaya.buildlogic.sjyPlugin

plugins {
    id("com.sanjaya.buildlogic.jvm.lib")
}

pluginManager.apply(project.sjyPlugin("koin-compiler"))

dependencies {
    add("implementation", project.sjyLibrary("koin-core"))
    add("implementation", project.sjyLibrary("koin-annotation"))
}

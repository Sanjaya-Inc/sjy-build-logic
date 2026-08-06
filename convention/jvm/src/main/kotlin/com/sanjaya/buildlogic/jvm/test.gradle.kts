package com.sanjaya.buildlogic.jvm

import com.sanjaya.buildlogic.sjyLibrary

dependencies {
    add("testImplementation", project.sjyLibrary("kotlin-test"))
    add("testImplementation", project.sjyLibrary("kotlin-testJunit"))
}

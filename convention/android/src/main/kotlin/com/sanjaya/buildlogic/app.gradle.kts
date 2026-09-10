package com.sanjaya.buildlogic

import com.android.build.api.dsl.ApplicationExtension

plugins {
    id("com.android.application")
    id("com.sanjaya.buildlogic.common")
}

configure<ApplicationExtension> {
    buildTypes.named("release") {
        optimization.enable = true
    }
}

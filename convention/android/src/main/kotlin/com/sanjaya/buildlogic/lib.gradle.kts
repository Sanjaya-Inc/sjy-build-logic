package com.sanjaya.buildlogic

import com.android.build.api.dsl.LibraryExtension

plugins {
    id("com.android.library")
    id("com.sanjaya.buildlogic.common")
}

configure<LibraryExtension> {
    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

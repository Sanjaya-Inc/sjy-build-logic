package com.sanjaya.buildlogic


println("[Build Logic][FirebaseSetup] Setting up firebase for project: ${project.name}")

val isApp = pluginManager.hasPlugin("com.android.application")

if (isApp) {
    pluginManager.apply(project.sjyPlugin("gms-services"))
    pluginManager.apply(project.sjyPlugin("crashlytics"))
}

dependencies {
    add("implementation", dependencies.platform(project.sjyLibrary("firebase-bom")))
    add("implementation", project.sjyLibrary("firebase-analytics"))
    add("implementation", project.sjyLibrary("firebase-crashlytics"))
    add("implementation", project.sjyLibrary("firebase-messaging"))
    add("implementation", project.sjyLibrary("firebase-config"))
    add("implementation", project.sjyLibrary("firebase-auth"))
}

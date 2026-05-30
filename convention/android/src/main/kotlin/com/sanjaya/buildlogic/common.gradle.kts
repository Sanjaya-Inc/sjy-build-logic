package com.sanjaya.buildlogic


pluginManager.apply("com.sanjaya.buildlogic.target")

println("[Build Logic][AndroidKotlinSetup] Setting up Kotlin for project: ${project.name}")
pluginManager.apply("kotlin-parcelize")
pluginManager.apply(project.sjyPlugin("kotlin-serialization"))

println("[Build Logic][KspSetup] Setting up ksp for project: ${project.name}")
runCatching {
    pluginManager.apply(project.sjyPlugin("ksp"))
}.onFailure { e ->
    println("[Build Logic][KspSetup] [ERROR] Failed to apply KSP plugin: ${e.message}")
    println(
        "[Build Logic][KspSetup] Please ensure ksp is added to the root build.gradle.kts: " +
            "alias(sjy.plugins.ksp) apply false"
    )
}.onSuccess {
    println("[Build Logic][KspSetup] Successfully applied ksp plugin")
}

println("[Build Logic][KtorfitSetup] Setting up android remote data for project: ${project.name}")
pluginManager.apply(project.sjyPlugin("ktorfit"))

println("[Build Logic][AndroidKoinSetup] Setting up Koin Android for project: ${project.name}")
pluginManager.apply(project.sjyPlugin("koin-compiler"))
extensions.configure<org.koin.compiler.plugin.KoinGradleExtension> {
    compileSafety.set(false)
    strictSafety.set(false)
    userLogs.set(true)
}

dependencies {
    add("implementation", project.sjyLibrary("kotlin-serialization"))
    add("implementation", project.sjyLibrary("coroutines-core"))
    add("implementation", project.sjyLibrary("coroutines-android"))
    add("implementation", project.sjyLibrary("kotlin-immutable"))
    add("implementation", project.sjyLibrary("kotlin-date"))

    add("implementation", dependencies.platform(project.sjyLibrary("okhttp-bom")))
    add("implementation", project.sjyLibrary("ktorfit"))
    add("implementation", project.sjyLibrary("okhttp"))
    add("implementation", project.sjyLibrary("okhttp-log"))
    add("implementation", project.sjyLibrary("ktor-okhttp"))
    add("implementation", project.sjyLibrary("ktor-serialization"))
    add("implementation", project.sjyLibrary("ktor-content-negotiation"))
    add("implementation", project.sjyLibrary("ktor-logging"))

    add("implementation", project.sjyLibrary("mmkv"))
    add("implementation", project.sjyLibrary("store"))

    add("implementation", project.sjyLibrary("app-startup"))

    add("implementation", dependencies.platform(project.sjyLibrary("koin-bom")))
    add("implementation", project.sjyLibrary("koin-core-viewmodel"))
    add("implementation", project.sjyLibrary("koin-android"))
    add("implementation", project.sjyLibrary("koin-android-compat"))
    add("implementation", project.sjyLibrary("koin-androidx-workmanager"))
    add("implementation", project.sjyLibrary("koin-androidx-compose"))
    add("implementation", project.sjyLibrary("koin-androidx-compose-navigation"))
    add("implementation", project.sjyLibrary("koin-compose-viewmodel-navigation"))
    add("implementation", project.sjyLibrary("koin-annotation"))
}

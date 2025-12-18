plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    alias(sjy.plugins.ksp)
    alias(sjy.plugins.jacoco)
    kotlin("jvm") version sjy.versions.kotlin.core
}

dependencies {
    implementation(platform(sjy.koin.bom))
    implementation(sjy.bundles.koin.nonandroid)
    implementation(sjy.koin.annotation)
    ksp(sjy.koin.ksp)
    compileOnly(sjy.plugin.agp)
    compileOnly(sjy.plugin.kgp)
    compileOnly(sjy.plugin.ksp)
    compileOnly(sjy.plugin.compose)
    implementation(sjy.plugin.detekt)
    testImplementation(sjy.junit.jupiter)
    testImplementation(sjy.junit.jupiter.api)
    testImplementation(sjy.mockk)
    testRuntimeOnly(sjy.junit.jupiter.engine)
    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
}

extensions.configure<JacocoPluginExtension> {
    toolVersion = sjy.versions.jacoco.get()
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Test>().configureEach {
    if (name.startsWith("test") && name.endsWith("UnitTest")) {
        extensions.configure(JacocoTaskExtension::class) {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }
}

afterEvaluate {
    tasks.withType<JacocoReport> {
        group = "verification"
        val testTask = tasks.named("test")
        dependsOn(testTask)
        classDirectories.setFrom(
            fileTree(layout.buildDirectory.dir("classes/kotlin/main/com"))
        )
        val sources = listOf(
            layout.projectDirectory.file("src/main/kotlin")
        )
        sourceDirectories.setFrom(sources)
        executionData.setFrom(
            layout.buildDirectory.dir("jacoco").get()
                .asFileTree.matching { include("**/test.exec") }
        )
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3)
    }
}

gradlePlugin {
    plugins {
        // region android buildlogic
        register("androidTarget") {
            id = "com.sanjaya.buildlogic.target"
            implementationClass = "com.sanjaya.buildlogic.android.plugins.AndroidTargetConventionPlugin"
        }
        register("androidApp") {
            id = "com.sanjaya.buildlogic.app"
            implementationClass = "com.sanjaya.buildlogic.android.plugins.AndroidAppConventionPlugin"
        }
        register("androidLib") {
            id = "com.sanjaya.buildlogic.lib"
            implementationClass = "com.sanjaya.buildlogic.android.plugins.AndroidLibConventionPlugin"
        }
        register("androidCompose") {
            id = "com.sanjaya.buildlogic.compose"
            implementationClass = "com.sanjaya.buildlogic.android.plugins.AndroidComposeConventionPlugin"
        }
        register("detekt") {
            id = "com.sanjaya.buildlogic.detekt"
            implementationClass = "com.sanjaya.buildlogic.common.plugins.DetektConventionPlugin"
        }
        register("androidFirebase") {
            id = "com.sanjaya.buildlogic.firebase"
            implementationClass = "com.sanjaya.buildlogic.android.plugins.FirebasePlugin"
        }
        register("androidTest") {
            id = "com.sanjaya.buildlogic.test"
            implementationClass = "com.sanjaya.buildlogic.android.plugins.TestConventionPlugin"
        }
        // endregion

        // region multiplatform buildlogic
        register("multiplatformApp") {
            id = "com.sanjaya.buildlogic.multiplatform.app"
            implementationClass = "com.sanjaya.buildlogic.multiplatform.plugins.KmpAppConventionPlugin"
        }
        register("multiplatformLib") {
            id = "com.sanjaya.buildlogic.multiplatform.lib"
            implementationClass = "com.sanjaya.buildlogic.multiplatform.plugins.KmpLibConventionPlugin"
        }
        register("composeMultiplatform") {
            id = "com.sanjaya.buildlogic.multiplatform.cmp"
            implementationClass = "com.sanjaya.buildlogic.multiplatform.plugins.CmpConventionPlugin"
        }
        // endregion
    }
}

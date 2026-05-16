plugins {
    `kotlin-dsl`
    alias(sjy.plugins.ksp)
    alias(sjy.plugins.jacoco)
}

dependencies {
    implementation(project(":convention:core"))
    implementation(project(":convention:tools:api"))
    ksp(sjy.koin.ksp)
    implementation(sjy.plugin.detekt)
    ksp(sjy.autoservice.ksp)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3)
    }
}

ksp {
    arg("KOIN_CONFIG_CHECK", "false")
}

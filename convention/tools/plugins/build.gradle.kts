plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

dependencies {
    implementation(project(":convention:core"))
    implementation(project(":convention:tools:api"))
    runtimeOnly(project(":convention:tools:impl"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3)
    }
}

gradlePlugin {
    plugins {
        register("detekt") {
            id = "com.sanjaya.buildlogic.detekt"
            implementationClass = "com.sanjaya.buildlogic.tools.DetektConventionPlugin"
        }
    }
}

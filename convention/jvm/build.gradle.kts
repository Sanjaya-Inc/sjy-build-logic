plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

dependencies {
    implementation(project(":convention:core"))
    implementation(project(":convention:tools"))
    implementation(sjy.plugin.kgp)
    implementation(sjy.plugin.kotlin.serialization)
    implementation(sjy.koin.compiler.plugin)
}

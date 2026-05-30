plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

dependencies {
    implementation(project(":convention:core"))
    compileOnly(sjy.plugin.agp)
    compileOnly(sjy.plugin.ksp)
    compileOnly(sjy.plugin.kgp)
}

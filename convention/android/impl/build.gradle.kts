plugins {
    `kotlin-dsl`
    alias(sjy.plugins.ksp)
}

dependencies {
    implementation(project(":convention:core"))
    implementation(project(":convention:android:api"))
    implementation(platform(sjy.koin.bom))
    implementation(sjy.koin.core)
    implementation(sjy.koin.annotation)
    ksp(sjy.koin.ksp)
    compileOnly(sjy.plugin.agp)
    compileOnly(sjy.plugin.ksp)
    compileOnly(sjy.plugin.compose)
    ksp(sjy.autoservice.ksp)
    implementation(sjy.autoservice.annotation)
}

ksp {
    arg("KOIN_CONFIG_CHECK", "false")
}

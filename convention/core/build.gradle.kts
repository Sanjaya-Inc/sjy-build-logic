plugins {
    `kotlin-dsl`
    alias(sjy.plugins.ksp)
}

dependencies {
    api(platform(sjy.koin.bom))
    api(sjy.koin.core)
    api(sjy.koin.annotation)
    ksp(sjy.koin.ksp)
    api(sjy.plugin.kgp)
    ksp(sjy.autoservice.ksp)
    api(sjy.autoservice.annotation)
}

ksp {
    arg("KOIN_CONFIG_CHECK", "false")
}

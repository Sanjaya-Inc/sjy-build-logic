plugins {
    alias(sjy.plugins.buildlogic.multiplatform.lib)
    alias(sjy.plugins.buildlogic.multiplatform.cmp)
    alias(sjy.plugins.buildconfig.kmp)
    alias(sjy.plugins.buildlogic.detekt)
}

kotlin {
    android {
        namespace = "core.presentation"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.utils)
            implementation(sjy.koin.annotation)
            implementation(sjy.koin.core.coroutines)
        }
    }
}

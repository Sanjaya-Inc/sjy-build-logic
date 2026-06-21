plugins {
    alias(sjy.plugins.buildlogic.multiplatform.lib)
    alias(sjy.plugins.buildconfig.kmp)
    alias(sjy.plugins.buildlogic.detekt)
}

kotlin {
    android {
        namespace = "core.data.pref.impl"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.utils)
            implementation(projects.core.dataPref.api)
            implementation(sjy.datastore)
            implementation(sjy.datastore.core)
            implementation(sjy.datastore.preferences)
        }
    }
}

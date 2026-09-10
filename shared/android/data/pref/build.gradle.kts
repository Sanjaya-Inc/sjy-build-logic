plugins {
    alias(sjy.plugins.buildlogic.lib)
}

android {
    namespace = "core.pref"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(project(":core:utils"))
    implementation(sjy.datastore)
}

plugins {
    alias(sjy.plugins.buildlogic.lib)
}

android {
    namespace = "core.local"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(project(":core:utils"))
    implementation(project(":core:data:pref"))
    implementation(sjy.bundles.room)
    ksp(sjy.room.compiler)
    testImplementation(sjy.room.test)
}

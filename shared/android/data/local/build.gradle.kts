plugins {
    alias(sjy.plugins.buildlogic.lib)
}

android {
    namespace = "core.local"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":core:utils"))
    implementation(project(":core:data:pref"))
    api(sjy.bundles.room)
    ksp(sjy.room.compiler)
    testImplementation(sjy.room.test)
}

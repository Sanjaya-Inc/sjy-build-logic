plugins {
    alias(sjy.plugins.buildlogic.lib)
}

android {
    namespace = "core.utils"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

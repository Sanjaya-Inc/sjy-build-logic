plugins {
    alias(sjy.plugins.buildlogic.multiplatform.lib)
    alias(sjy.plugins.buildconfig.kmp)
    alias(sjy.plugins.buildlogic.detekt)
}

kotlin {
    android {
        namespace = "core.data.pref.api"
    }
}

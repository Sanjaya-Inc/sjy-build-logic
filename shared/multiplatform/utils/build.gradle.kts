plugins {
    alias(sjy.plugins.buildlogic.multiplatform.lib)
    alias(sjy.plugins.buildlogic.multiplatform.cmp)
}

kotlin {
    android {
        namespace = "core.utils"
    }
}

ktorfit {
    compilerPluginVersion.set("2.3.3")
}

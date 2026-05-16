plugins {
    alias(sjy.plugins.buildlogic.multiplatform.lib)
    alias(sjy.plugins.buildlogic.multiplatform.cmp)
    alias(sjy.plugins.buildlogic.detekt)
}

kotlin {
    android {
        namespace = "core.utils"
    }
}

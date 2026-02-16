plugins {
    alias(sjy.plugins.buildlogic.multiplatform.lib)
    alias(sjy.plugins.buildlogic.multiplatform.cmp)
}

kotlin {
    android {
        namespace = "core.presentation"
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.utils)
        }
    }
}

ktorfit {
    compilerPluginVersion.set("2.3.3")
}

plugins {
    alias(sjy.plugins.buildlogic.multiplatform.lib)
    alias(sjy.plugins.buildconfig.kmp)
}

kotlin {
    android {
        namespace = "core.data.pref"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.utils)
            implementation(project.dependencies.platform(sjy.supabase.bom))
            implementation(sjy.bundles.supabase.core)
        }
    }
}

ktorfit {
    compilerPluginVersion.set("2.3.3")
}

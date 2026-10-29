import com.sanjaya.buildlogic.quotedLocalProperty

plugins {
    alias(sjy.plugins.buildlogic.multiplatform.lib)
    alias(sjy.plugins.buildconfig.kmp)
    alias(sjy.plugins.buildlogic.detekt)
}

kotlin {
    android {
        namespace = "core.supabase"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.utils)
            implementation(project.dependencies.platform(sjy.supabase.bom))
            implementation(sjy.bundles.supabase.core)
        }
    }
}

buildConfig {
    className("SupabaseConfig")
    packageName("core.supabase")
    buildConfigField("String", "SUPABASE_URL", quotedLocalProperty("SUPABASE_URL"))
    buildConfigField("String", "SUPABASE_KEY", quotedLocalProperty("SUPABASE_KEY"))
    buildConfigField("String", "GOOGLE_CLIENT_ID", quotedLocalProperty("GOOGLE_CLIENT_ID"))
}

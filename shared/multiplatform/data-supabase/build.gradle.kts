import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(sjy.plugins.buildlogic.multiplatform.lib)
    alias(sjy.plugins.buildconfig.kmp)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(FileInputStream(localPropertiesFile))
    }
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

ktorfit {
    compilerPluginVersion.set("2.3.3")
}

buildConfig {
    className("SupabaseConfig")
    packageName("core.supabase")
    buildConfigField(
        "String",
        "SUPABASE_URL",
        "\"${localProperties.getProperty("SUPABASE_URL", "")}\""
    )
    buildConfigField(
        "String",
        "SUPABASE_KEY",
        "\"${localProperties.getProperty("SUPABASE_KEY", "")}\""
    )
    buildConfigField(
        "String",
        "GOOGLE_CLIENT_ID",
        "\"${localProperties.getProperty("GOOGLE_CLIENT_ID", "")}\""
    )
}

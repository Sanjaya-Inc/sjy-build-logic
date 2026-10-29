import com.sanjaya.buildlogic.quotedLocalProperty

plugins {
    alias(sjy.plugins.buildlogic.multiplatform.lib)
    alias(sjy.plugins.buildlogic.multiplatform.cmp)
    alias(sjy.plugins.buildconfig.kmp)
    alias(sjy.plugins.buildlogic.detekt)
}

kotlin {
    android {
        namespace = "core.utils"
    }
    sourceSets {
        androidMain.dependencies {
            implementation(project.dependencies.platform(sjy.firebase.bom))
            implementation(sjy.firebase.auth)
            implementation(sjy.androidx.credentials)
            implementation(sjy.androidx.credentials.play.services.auth)
            implementation(sjy.googleid)
        }
        commonTest.dependencies {
            implementation(sjy.coroutines.test)
        }
    }
}

buildConfig {
    className("GoogleAuthConfig")
    packageName("core.utils.auth")
    buildConfigField("String", "GOOGLE_CLIENT_ID", quotedLocalProperty("GOOGLE_CLIENT_ID"))
}

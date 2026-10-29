import com.android.build.api.variant.BuildConfigField
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.sanjaya.buildlogic.quotedLocalProperty

plugins {
    alias(sjy.plugins.buildlogic.lib)
}

android {
    namespace = "core.network"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
    }
}

extensions.configure<LibraryAndroidComponentsExtension> {
    onVariants { variant ->
        val fields = checkNotNull(variant.buildConfigFields) {
            "buildConfigFields is null; enable buildFeatures.buildConfig"
        }
        fields.put("BASE_URL", buildConfigString("api.base.url"))
        fields.put("SSL_PINS", buildConfigString("api.ssl.pins"))
    }
}

fun buildConfigString(key: String) =
    quotedLocalProperty(key).map { literal ->
        BuildConfigField("String", literal, null)
    }

dependencies {
    debugImplementation(sjy.chucker)
    releaseImplementation(sjy.chucker.no.op)
    implementation(project(":core:utils"))
}

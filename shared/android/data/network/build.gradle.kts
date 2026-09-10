import java.io.FileInputStream
import java.util.Properties
import kotlin.apply

plugins {
    alias(sjy.plugins.buildlogic.lib)
}

val localProperties = Properties().apply {
    load(FileInputStream(layout.settingsDirectory.file("local.properties").asFile))
}

android {
    namespace = "core.network"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val baseUrl = localProperties.getProperty("api.base.url") ?: ""
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        val sslPins = localProperties.getProperty("api.ssl.pins") ?: ""
        buildConfigField("String", "SSL_PINS", "\"$sslPins\"")
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    debugImplementation(sjy.chucker)
    releaseImplementation(sjy.chucker.no.op)
    implementation(project(":core:utils"))
}

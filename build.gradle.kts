plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin.nonandroid)
    implementation(libs.koin.annotation)
    ksp(libs.koin.ksp)
    compileOnly(libs.plugin.agp)
    compileOnly(libs.plugin.kgp)
    compileOnly(libs.plugin.ksp)
    compileOnly(libs.plugin.compose)
    compileOnly(libs.plugin.detekt)
    compileOnly(libs.plugin.detekt)
}

gradlePlugin {
    val androidTarget by plugins.creating {
        id = "com.sanjaya.buildlogic.target"
        implementationClass = "com.sanjaya.buildlogic.plugins.AndroidTargetConventionPlugin"
    }
    val app by plugins.creating {
        id = "com.sanjaya.buildlogic.app"
        implementationClass = "com.sanjaya.buildlogic.plugins.AndroidAppConventionPlugin"
    }
    val lib by plugins.creating {
        id = "com.sanjaya.buildlogic.lib"
        implementationClass = "com.sanjaya.buildlogic.plugins.AndroidLibConventionPlugin"
    }
    val compose by plugins.creating {
        id = "com.sanjaya.buildlogic.compose"
        implementationClass = "com.sanjaya.buildlogic.plugins.AndroidComposeConventionPlugin"
    }
    val detekt by plugins.creating {
        id = "com.sanjaya.buildlogic.detekt"
        implementationClass = "com.sanjaya.buildlogic.plugins.DetektConventionPlugin"
    }
    val firebase by plugins.creating {
        id = "com.sanjaya.buildlogic.firebase"
        implementationClass = "com.sanjaya.buildlogic.plugins.FirebasePlugin"
    }
    val test by plugins.creating {
        id = "com.sanjaya.buildlogic.test"
        implementationClass = "com.sanjaya.buildlogic.plugins.TestConventionPlugin"
    }
}

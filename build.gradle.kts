plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    alias(core.plugins.ksp)
}

dependencies {
    implementation(platform(core.koin.bom))
    implementation(core.bundles.koin.nonandroid)
    implementation(core.koin.annotation)
    ksp(core.koin.ksp)
    compileOnly(core.plugin.agp)
    compileOnly(core.plugin.kgp)
    compileOnly(core.plugin.ksp)
    compileOnly(core.plugin.compose)
    compileOnly(core.plugin.detekt)
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
}

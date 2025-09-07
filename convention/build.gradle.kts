plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    alias(sjy.plugins.ksp)
}

dependencies {
    implementation(platform(sjy.koin.bom))
    implementation(sjy.bundles.koin.nonandroid)
    implementation(sjy.koin.annotation)
    ksp(sjy.koin.ksp)
    compileOnly(sjy.plugin.agp)
    compileOnly(sjy.plugin.kgp)
    compileOnly(sjy.plugin.ksp)
    compileOnly(sjy.plugin.compose)
    compileOnly(sjy.plugin.detekt)
    compileOnly(sjy.plugin.detekt)
}

gradlePlugin {
    plugins {
        // region android buildlogic
        register("androidTarget") {
            id = "com.sanjaya.buildlogic.target"
            implementationClass = "com.sanjaya.buildlogic.android.plugins.AndroidTargetConventionPlugin"
        }
        register("androidApp") {
            id = "com.sanjaya.buildlogic.app"
            implementationClass = "com.sanjaya.buildlogic.android.plugins.AndroidAppConventionPlugin"
        }
        register("androidLib") {
            id = "com.sanjaya.buildlogic.lib"
            implementationClass = "com.sanjaya.buildlogic.android.plugins.AndroidLibConventionPlugin"
        }
        register("androidCompose") {
            id = "com.sanjaya.buildlogic.compose"
            implementationClass = "com.sanjaya.buildlogic.android.plugins.AndroidComposeConventionPlugin"
        }
        register("detekt") {
            id = "com.sanjaya.buildlogic.detekt"
            implementationClass = "com.sanjaya.buildlogic.common.plugins.DetektConventionPlugin"
        }
        register("androidFirebase") {
            id = "com.sanjaya.buildlogic.firebase"
            implementationClass = "com.sanjaya.buildlogic.android.plugins.FirebasePlugin"
        }
        register("androidTest") {
            id = "com.sanjaya.buildlogic.test"
            implementationClass = "com.sanjaya.buildlogic.android.plugins.TestConventionPlugin"
        }
        // endregion

        // region multiplatform buildlogic
        register("multiplatformApp") {
            id = "com.sanjaya.buildlogic.multiplatform.app"
            implementationClass = "com.sanjaya.buildlogic.multiplatform.plugins.KmpAppConventionPlugin"
        }
        register("multiplatformLib") {
            id = "com.sanjaya.buildlogic.multiplatform.lib"
            implementationClass = "com.sanjaya.buildlogic.multiplatform.plugins.KmpLibConventionPlugin"
        }
        register("composeMultiplatform") {
            id = "com.sanjaya.buildlogic.multiplatform.cmp"
            implementationClass = "com.sanjaya.buildlogic.multiplatform.plugins.CmpConventionPlugin"
        }
        // endregion
    }
}

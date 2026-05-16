plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

dependencies {
    implementation(project(":convention:core"))
    implementation(project(":convention:android:api"))
    runtimeOnly(project(":convention:android:impl"))
}

gradlePlugin {
    plugins {
        register("androidTarget") {
            id = "com.sanjaya.buildlogic.target"
            implementationClass = "com.sanjaya.buildlogic.android.AndroidTargetConventionPlugin"
        }
        register("androidApp") {
            id = "com.sanjaya.buildlogic.app"
            implementationClass = "com.sanjaya.buildlogic.android.AndroidAppConventionPlugin"
        }
        register("androidLib") {
            id = "com.sanjaya.buildlogic.lib"
            implementationClass = "com.sanjaya.buildlogic.android.AndroidLibConventionPlugin"
        }
        register("androidCompose") {
            id = "com.sanjaya.buildlogic.compose"
            implementationClass = "com.sanjaya.buildlogic.android.AndroidComposeConventionPlugin"
        }
        register("androidFirebase") {
            id = "com.sanjaya.buildlogic.firebase"
            implementationClass = "com.sanjaya.buildlogic.android.FirebasePlugin"
        }
        register("androidTest") {
            id = "com.sanjaya.buildlogic.test"
            implementationClass = "com.sanjaya.buildlogic.android.TestConventionPlugin"
        }
    }
}

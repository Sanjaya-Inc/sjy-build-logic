plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

dependencies {
    implementation(project(":convention:core"))
    implementation(project(":convention:multiplatform:api"))
    runtimeOnly(project(":convention:multiplatform:impl"))
}

gradlePlugin {
    plugins {
        register("multiplatformLib") {
            id = "com.sanjaya.buildlogic.multiplatform.lib"
            implementationClass = "com.sanjaya.buildlogic.multiplatform.KmpLibConventionPlugin"
        }
        register("composeMultiplatform") {
            id = "com.sanjaya.buildlogic.multiplatform.cmp"
            implementationClass = "com.sanjaya.buildlogic.multiplatform.CmpConventionPlugin"
        }
    }
}

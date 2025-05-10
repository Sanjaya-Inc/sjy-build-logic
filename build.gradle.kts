plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    alias(core.plugins.ksp)
}

dependencies {
    compileOnly(core.plugin.agp)
    compileOnly(core.plugin.kgp)
    compileOnly(core.plugin.ksp)
    compileOnly(core.plugin.compose)
    compileOnly(core.plugin.detekt)
}

gradlePlugin {
    val androidTarget by plugins.creating {
        id = "com.sanjaya.buildlogic.target"
        implementationClass = "com.sanjaya.buildlogic.AndroidTargetConventionPlugin"
    }
    val app by plugins.creating {
        id = "com.sanjaya.buildlogic.app"
        implementationClass = "com.sanjaya.buildlogic.AndroidAppConventionPlugin"
    }
    val lib by plugins.creating {
        id = "com.sanjaya.buildlogic.lib"
        implementationClass = "com.sanjaya.buildlogic.AndroidLibConventionPlugin"
    }
    val compose by plugins.creating {
        id = "com.sanjaya.buildlogic.compose"
        implementationClass = "com.sanjaya.buildlogic.AndroidComposeConventionPlugin"
    }
    val apollo by plugins.creating {
        id = "com.sanjaya.buildlogic.apollo"
        implementationClass = "com.sanjaya.buildlogic.ApolloConventionPlugin"
    }
    val detekt by plugins.creating {
        id = "com.sanjaya.buildlogic.detekt"
        implementationClass = "com.sanjaya.buildlogic.DetektConventionPlugin"
    }
    val kotlinMultiplatform by plugins.creating {
        id = "com.sanjaya.buildlogic.kotlin-multiplatform"
        implementationClass = "com.sanjaya.buildlogic.KotlinMultiplatformConventionPlugin"
    }
}

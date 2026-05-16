pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("sjy") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "sjy-build-logic"
include(":convention")
include(":convention:core")
include(":convention:android:api")
include(":convention:android:impl")
include(":convention:android:plugins")
include(":convention:multiplatform:api")
include(":convention:multiplatform:impl")
include(":convention:multiplatform:plugins")
include(":convention:tools:api")
include(":convention:tools:impl")
include(":convention:tools:plugins")

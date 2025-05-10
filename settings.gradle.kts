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
        create("core") {
            from(files("../sjy-version-catalog-core/core.versions.toml"))
        }
    }
}

rootProject.name = "sjy-build-logic"
include(":buildlogic")

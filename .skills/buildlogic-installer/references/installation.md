# Installation Guide for sjy-build-logic

This guide explains how to install and configure `sjy-build-logic` convention plugins in both Android-only and Kotlin/Compose Multiplatform (CMP/KMP) projects.

---

## 1. Prerequisites

Ensure the project meets these baseline requirements:
- **Gradle Version**: `9.1.0`+ (Gradle `9.4.1`+ recommended for AGP 9.2+)
- **JDK Version**: `17` minimum
- **Kotlin Version (KGP)**: `2.2.10`+ (e.g., Kotlin `2.4.0`)
- **Android Gradle Plugin (AGP)**: `9.0.0`+ (Kotlin is compiled natively by AGP; do NOT apply `org.jetbrains.kotlin.android` inside modules)

---

## 2. Base Configuration Steps

### Step 1: Add as Git Submodule
Add the repository as a submodule:
```bash
git submodule add https://github.com/Sanjaya-Inc/sjy-build-logic.git sjy-build-logic
git submodule update --init --recursive
```

### Step 2: Configure `settings.gradle.kts`
1. Register the composite build logic. Add `includeBuild("sjy-build-logic")` to `pluginManagement` at the very beginning:
   ```kotlin
   pluginManagement {
       includeBuild("sjy-build-logic")
       repositories {
           google()
           mavenCentral()
           gradlePluginPortal()
       }
   }
   ```
2. Configure the `sjy` version catalog. In `dependencyResolutionManagement`, register the catalog pointing to the submodule:
   ```kotlin
   dependencyResolutionManagement {
       repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
       repositories {
           google()
           mavenCentral()
       }
       versionCatalogs {
           create("sjy") {
               from(files("sjy-build-logic/gradle/libs.versions.toml"))
           }
       }
   }
   ```

### Step 3: Configure Root `build.gradle.kts`
Declare plugin aliases with `apply false` (or `apply true` for static analysis/detekt) to make them available in project submodules:
```kotlin
plugins {
    alias(sjy.plugins.android.application) apply false
    alias(sjy.plugins.android.library) apply false
    alias(sjy.plugins.compose.multiplatform) apply false
    alias(sjy.plugins.kotlin.compose) apply false
    alias(sjy.plugins.kotlin.multiplatform) apply false
    alias(sjy.plugins.ksp) apply false
    alias(sjy.plugins.detekt) apply true // Applied globally at root
    alias(sjy.plugins.ktorfit) apply false
    alias(sjy.plugins.kotlin.serialization) apply false
    alias(sjy.plugins.android.kotlin.multiplatform.library) apply false
    alias(sjy.plugins.android.lint) apply false
}
```

---

## 3. Project-Specific Setup Adjustments

### Scenario A: Android-Only Projects
Apply these conventions to Android app and library modules:
- **Android App Module** (`:app` or `:androidApp`):
  ```kotlin
  plugins {
      alias(sjy.plugins.buildlogic.app)
      alias(sjy.plugins.buildlogic.compose) // Optional, if Compose UI is used
      alias(sjy.plugins.buildlogic.detekt)
  }
  ```
- **Android Library Module**:
  ```kotlin
  plugins {
      alias(sjy.plugins.buildlogic.lib)
      alias(sjy.plugins.buildlogic.compose) // Optional, if Compose UI is used
      alias(sjy.plugins.buildlogic.detekt)
  }
  ```

### Scenario B: Compose Multiplatform (CMP/KMP) Projects
Apply KMP-specific conventions:
- **KMP Library Module** (e.g., `:shared` or `:composeApp`):
  ```kotlin
  plugins {
      alias(sjy.plugins.buildlogic.multiplatform.lib)
      alias(sjy.plugins.buildlogic.multiplatform.cmp) // Optional, if Compose Multiplatform UI is shared
      alias(sjy.plugins.buildlogic.detekt)
  }
  ```
- **Android Runner Module** (`:androidApp`):
  Must be a separate module applying `sjy.plugins.buildlogic.app` and depending on the KMP library. Under AGP 9.0+, KMP modules cannot be application modules.

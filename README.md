# Build Logic

This module provides a set of Gradle plugins to enforce conventions and simplify build configurations for Android projects. It helps maintain consistency and reduces boilerplate across multiple modules.

---

## How to Use

### 1. Add as a Git Submodule

First, add the `sjy-build-logic` repository as a submodule to your root project:

```bash
git submodule add https://github.com/Sanjaya-Inc/sjy-build-logic.git sjy-build-logic
```

---

### 2. Run the Installation Script (Recommended)

After adding the submodule, run the appropriate script for your OS. This will automatically configure your `settings.gradle.kts` and apply convention plugins to all Android modules.

#### For macOS and Linux

```bash
chmod +x sjy-build-logic/installation.sh
./sjy-build-logic/installation.sh
```

#### For Windows

Open PowerShell and run:

```powershell
./sjy-build-logic/installation.ps1
```

> **Note:** If scripts are disabled, run:
>
> ```powershell
> Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope Process
> ```

---

## Manual Configuration

If you prefer or need to configure the build logic manually, follow these steps:

### 1. Add Submodule

```bash
git submodule add https://github.com/Sanjaya-Inc/sjy-build-logic.git sjy-build-logic
```

---

### 2. Update `settings.gradle.kts`

Include the build logic from the submodule:

```kotlin
pluginManagement {
    includeBuild("sjy-build-logic")
    // Rest of your plugin management config
}

dependencyResolutionManagement {
    // Rest of your dependency resolution management config
    versionCatalogs {
        create("sjy") {
            from(files("sjy-build-logic/gradle/libs.versions.toml"))
        }
    }
}
```

---

### 3. Update `libs.versions.toml`

In your `gradle/libs.versions.toml`, add:

```toml
[versions]
compile-sdk = "36"
min-sdk = "24"

[plugins]
sjy-detekt = { id = "com.sanjaya.buildlogic.detekt" }
sjy-lib = { id = "com.sanjaya.buildlogic.lib" }
sjy-compose = { id = "com.sanjaya.buildlogic.compose" }
sjy-app = { id = "com.sanjaya.buildlogic.app" }
sjy-firebase = { id = "com.sanjaya.buildlogic.firebase" }
```

---

### 4. Update Root `build.gradle.kts`

Add plugin aliases in the root `build.gradle.kts` file:

```kotlin
plugins {
    alias(sjy.plugins.android.application) apply false
    alias(sjy.plugins.android.library) apply false
    alias(sjy.plugins.kotlin.android) apply false
    alias(sjy.plugins.kotlin.compose) apply false
    alias(sjy.plugins.ksp) apply false
    alias(sjy.plugins.detekt) apply true
    alias(sjy.plugins.ktorfit) apply false
    alias(sjy.plugins.gms.services) apply false
    alias(sjy.plugins.crashlytics) apply false
    alias(sjy.plugins.lumo) apply false
    alias(sjy.plugins.buildlogic.detekt) apply true
}
```

---

### 5. Apply Plugins in Modules

Then apply plugins in each module’s `build.gradle.kts` file as needed:

```kotlin
plugins {
    alias(sjy.plugins.buildlogic.app)
    alias(sjy.plugins.buildlogic.lib)
    alias(sjy.plugins.buildlogic.compose)
    alias(sjy.plugins.buildlogic.detekt)
}
```

---

## Available Plugins

### Android Plugins

#### ✅ `AndroidAppConventionPlugin`

Applies common configurations for Android application modules:

* `compileSdkVersion`, `minSdkVersion`, `targetSdkVersion`
* **AGP 9.0+**: Built-in Kotlin support (no need for `kotlin-android` plugin)
* Use this for standalone Android apps or `androidApp` modules that consume KMP libraries

```kotlin
plugins {
    id("com.sanjaya.buildlogic.app")
}
```

---

#### ✅ `AndroidLibConventionPlugin`

Applies conventions for Android library modules:

* Library-specific compile/target SDK setup
* Build features
* Publishing configuration (if needed)

```kotlin
plugins {
    id("com.sanjaya.buildlogic.lib")
}
```

---

#### ✅ `AndroidComposeConventionPlugin`

Adds Jetpack Compose setup and dependencies automatically:

* Enables Compose in `buildFeatures`
* Applies compiler options
* Adds standard Compose BOM and core dependencies

```kotlin
plugins {
    id("com.sanjaya.buildlogic.compose")
}
```

---

#### ✅ `AndroidTargetConventionPlugin`

Applies target configurations that may affect the project or variants globally, e.g., build types or product flavors.

```kotlin
plugins {
    id("com.sanjaya.buildlogic.target")
}
```

---

### Kotlin Multiplatform Plugins

#### ✅ `KmpLibConventionPlugin`

**AGP 9.0+ Required**: Configures Kotlin Multiplatform library modules using the new `com.android.kotlin.multiplatform.library` plugin.

* Applies `kotlin-multiplatform` and `android-kotlin-multiplatform-library` plugins
* Configures Android, iOS targets
* Sets up Koin DI, Kotlin serialization, coroutines
* Includes common dependencies (napier, kotlin-test)

**Important**: With AGP 9.0+, KMP modules can only be libraries. For apps, create a separate `androidApp` module that depends on your KMP library.

```kotlin
plugins {
    id("com.sanjaya.buildlogic.multiplatform.lib")
}
```

---

#### ✅ `CmpConventionPlugin`

Configures Compose Multiplatform for KMP library modules:

* Applies Compose Multiplatform and Kotlin Compose plugins
* Adds Compose dependencies (runtime, foundation, material3, UI)
* Includes Orbit MVI, lifecycle, and navigation
* Android-specific Compose dependencies (activity-compose, tooling)

**Note**: This is now library-only. Use with `KmpLibConventionPlugin` for shared UI code.

```kotlin
plugins {
    id("com.sanjaya.buildlogic.multiplatform.lib")
    id("com.sanjaya.buildlogic.multiplatform.cmp")
}
```

---

### Common Plugins

#### ✅ `DetektConventionPlugin`

Adds Detekt static analysis with shared configuration:

* Loads `detekt-config.yml`
* Applies Detekt version and rules
* Registers Detekt tasks

```kotlin
plugins {
    id("com.sanjaya.buildlogic.detekt")
}
```

---

#### ✅ `FirebasePlugin`

Optional Firebase-specific setup:

* Applies Google Services and Crashlytics plugins
* Configures shared Firebase options

```kotlin
plugins {
    id("com.sanjaya.buildlogic.firebase")
}
```

---

## AGP 9.0 Migration Guide

### Key Changes in AGP 9.0

1. **Built-in Kotlin Support**: Android modules no longer need the `kotlin-android` plugin
2. **New KMP Plugin**: Use `com.android.kotlin.multiplatform.library` for KMP modules
3. **Separation of Concerns**: KMP modules are libraries only; apps must be separate modules

### Migration Steps

#### For Existing `composeApp` Modules

If you have a single `composeApp` module that combines KMP and Android app:

1. **Create a new `androidApp` module**:
   ```kotlin
   // androidApp/build.gradle.kts
   plugins {
       alias(sjy.plugins.buildlogic.app)
       alias(sjy.plugins.buildlogic.compose)
   }
   
   dependencies {
       implementation(project(":composeApp"))
   }
   ```

2. **Convert `composeApp` to a library**:
   ```kotlin
   // composeApp/build.gradle.kts
   plugins {
       alias(sjy.plugins.buildlogic.multiplatform.lib)
       alias(sjy.plugins.buildlogic.multiplatform.cmp)
   }
   ```

3. **Move Android-specific code**:
   - Move `MainActivity`, `Application` class to `androidApp`
   - Keep shared UI and business logic in `composeApp`

#### For New Projects

Structure your project with clear separation:

```
project/
├── androidApp/          # Android application entry point
│   └── build.gradle.kts # Uses buildlogic.app
├── composeApp/          # KMP library with shared code
│   └── build.gradle.kts # Uses buildlogic.multiplatform.lib + cmp
└── iosApp/              # iOS application entry point
```

### Removed Plugins

- ❌ `buildlogic-multiplatform-app` - No longer available (KMP modules are libraries only)
---

## Benefits

* **Centralized Configuration**
* **Consistency Across Modules**
* **Reduced Boilerplate**
* **Alias-Based Usage with `libs.versions.toml`**

---

## File Structure

```
sjy-build-logic/
├── build/                    # Gradle build output
├── config/                   # Optional shared config (e.g., Detekt rules)
├── gradle/                   # Gradle wrapper or version catalog
├── src/                      # Plugin implementation (Java/Kotlin)
├── .gitignore
├── build.gradle.kts          # Build script for the build logic module
├── installation.ps1          # Windows installation script
├── installation.sh           # macOS/Linux installation script
├── README.md                 # This documentation file
├── settings.gradle.kts       # Settings for the build logic module
```

---
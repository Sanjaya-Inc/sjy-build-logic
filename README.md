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
```

---

### 3. Update `libs.versions.toml`

In your `gradle/libs.versions.toml`, add:

```toml
[versions]
compile-sdk = "35"
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
    alias(libs.plugins.sjy.detekt) apply true
}
```

---

### 5. Apply Plugins in Modules

Then apply plugins in each module’s `build.gradle.kts` file as needed:

```kotlin
plugins {
    alias(libs.plugins.sjy.app)
    alias(libs.plugins.sjy.lib)
    alias(libs.plugins.sjy.compose)
    alias(libs.plugins.sjy.detekt)
}
```

---

## Available Plugins

### ✅ `AndroidAppConventionPlugin`

Applies common configurations for Android application modules:

* `compileSdkVersion`, `minSdkVersion`, `targetSdkVersion`

```kotlin
plugins {
    id("com.sanjaya.buildlogic.app")
}
```

---

### ✅ `AndroidLibConventionPlugin`

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

### ✅ `AndroidComposeConventionPlugin`

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

### ✅ `AndroidTargetConventionPlugin`

Applies target configurations that may affect the project or variants globally, e.g., build types or product flavors.

```kotlin
plugins {
    id("com.sanjaya.buildlogic.target")
}
```

---

### ✅ `DetektConventionPlugin`

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

### ✅ `FirebasePlugin`

Optional Firebase-specific setup:

* Applies Google Services and Crashlytics plugins
* Configures shared Firebase options

```kotlin
plugins {
    id("com.sanjaya.buildlogic.firebase")
}
```
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
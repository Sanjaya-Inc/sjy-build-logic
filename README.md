# Build Logic

This module provides a set of Gradle plugins to enforce conventions and simplify build configurations for Android projects. It helps maintain consistency and reduces boilerplate across multiple modules.

## How to Use

### 1. Add as a Git Submodule

First, add the `sjy-build-logic` repository as a submodule to your root project. This is a required first step.

```bash
git submodule add https://github.com/Sanjaya-Inc/sjy-build-logic.git sjy-build-logic
```

### 2. Run the Installation Script

After adding the submodule, run the appropriate script for your operating system to automate the rest of the setup. The script will configure your `settings.gradle.kts` file and apply the necessary convention plugins to your Android modules.

#### For macOS and Linux

First, you may need to grant execute permissions to the script. Then, run it.

```bash
chmod +x sjy-build-logic/installation.sh
./sjy-build-logic/installation.sh
```

#### For Windows

Open PowerShell and run the installation script.

```powershell
./sjy-build-logic/installation.ps1
```
> **Note for Windows Users:** If you encounter an error about scripts being disabled on the system, you may need to change the execution policy for the current process. You can do this by running the following command in PowerShell before executing the installation script:
> ```powershell
> Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope Process
> ```

---
## Manual Configuration

If you need to understand the changes made by the script or wish to perform the setup manually, here are the steps involved.

### 1. Include in `settings.gradle.kts`

First, add the `sjy-build-logic` repository as a submodule to your root project:

```bash
git submodule add https://github.com/Sanjaya-Inc/sjy-build-logic.git sjy-build-logic
```

This command will add the `sjy-build-logic` repository to your project and create a `.gitmodules` file to track the submodule.

### 2. Include in `settings.gradle.kts`

To include the `sjy-build-logic` in your project, add the following to your `settings.gradle.kts` file:

```kotlin
pluginManagement {
    includeBuild("sjy-build-logic")
    // Rest of config
}
```

This configuration tells Gradle to include the build logic from the `sjy-build-logic` module. The scripts also add a version catalog named `sjy` that points to the `libs.versions.toml` within the submodule.

### 3. Apply Plugins in `build.gradle.kts`

In your project level `build.gradle.kts` file, if you want to apply detekt plugins add this:

```kotlin
plugins {
    alias(core.plugins.detekt) apply true
    id("com.sanjaya.buildlogic.detekt") apply true
}
```

## Available Plugins

### `AndroidAppConventionPlugin`

This plugin applies conventions for Android application modules. It configures common settings such as:

-   `compileSdkVersion`
-   `minSdkVersion`
-   `targetSdkVersion`
-   `buildFeatures`
-   `packagingOptions`

To use this plugin, apply it in your `build.gradle.kts` file:

```kotlin
plugins {
    id("com.sanjaya.buildlogic.app")
}
```

### `AndroidLibConventionPlugin`

This plugin applies conventions for Android library modules. It configures common settings such as:

-   `compileSdkVersion`
-   `minSdkVersion`
-   `targetSdkVersion`
-   `buildFeatures`
-   `packagingOptions`

To use this plugin, apply it in your `build.gradle.kts` file:

```kotlin
plugins {
    id("com.sanjaya.buildlogic.lib")
}
```

### `AndroidComposeConventionPlugin`

This plugin applies conventions for Android modules using Jetpack Compose. It configures common settings such as:

-   Enabling Compose
-   Setting up Compose compiler
-   Adding Compose dependencies

To use this plugin, apply it in your `build.gradle.kts` file:

```kotlin
plugins {
    id("com.sanjaya.buildlogic.compose")
}
```

### `AndroidTargetConventionPlugin`

This plugin applies target conventions for Android.

To use this plugin, apply it in your `build.gradle.kts` file:

```kotlin
plugins {
    id("com.sanjaya.buildlogic.target")
}
```

### `DetektConventionPlugin`

This plugin applies Detekt static analysis to your project. It configures:

-   Detekt version
-   Detekt configuration file
-   Detekt tasks

To use this plugin, apply it in your `build.gradle.kts` file:

```kotlin
plugins {
    id("com.sanjaya.buildlogic.detekt")
}
```

## Benefits

-   **Centralized Configuration**: All build logic is managed in a single module.
-   **Consistency**: Ensures consistent build configurations across all modules in the project.
-   **Reduced Boilerplate**: Simplifies build files by extracting common configurations into plugins.
-   **Easy to Use**: Provides a simple and intuitive way to apply conventions.

## File Structure

-   `build.gradle.kts`: Contains plugin definitions and configurations.
-   `src/main/java/com/sanjaya/buildlogic/plugins/`: Contains the implementation of the convention plugins.
-   `README.md`: Documentation for using the build logic module.

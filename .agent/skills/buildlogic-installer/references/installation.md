# Installation Guide for sjy-build-logic

Install and wire convention plugins + optional shared modules into Android-only or KMP/CMP consumers.

---

## 1. Baselines (current repo)

| Baseline | Value | Source |
| :--- | :--- | :--- |
| Gradle | **9.8.0** | `gradle/wrapper/gradle-wrapper.properties` |
| AGP | **9.4.1** | `agp` in `gradle/libs.versions.toml` |
| Kotlin (KGP) | **2.4.20** | `kotlin-core` |
| JDK / `jvm-target` | **21** | `jvm-target` |
| KSP | 2.3.12 | `ksp` |
| Compose Multiplatform | 1.12.1 | `compose-multiplatform` |
| Detekt | 1.23.8 | `detekt` |
| `compile-sdk` / `min-sdk` | Consumer `libs` (e.g. 37 / 24) | Read via `sjyVersion` with `libs` fallback |

Minimum practical floor: Gradle 9.8+, JDK 21, AGP 9.0+, Kotlin 2.4+.

---

## 2. Base Configuration

### Step 1: Git submodule

```bash
git submodule add https://github.com/Sanjaya-Inc/sjy-build-logic.git sjy-build-logic
git submodule update --init --recursive
```

Optional installer scripts (Android-module plugin wiring):

```bash
chmod +x sjy-build-logic/installation.sh && ./sjy-build-logic/installation.sh
# Windows: ./sjy-build-logic/installation.ps1
```

Scripts help bootstrap; still verify settings/root plugins against this doc.

### Step 2: `settings.gradle.kts`

```kotlin
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("sjy-build-logic")
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
            from(files("sjy-build-logic/gradle/libs.versions.toml"))
        }
    }
}
```

Consumer must also define `compile-sdk` and `min-sdk` (and for iOS sync, `app-version-name` / `app-version-code`) in its own `gradle/libs.versions.toml` — convention plugins resolve them via `sjy` then `libs`.

### Step 3: Root `build.gradle.kts`

Declare upstream plugins with `apply false` so modules can resolve them. Detekt may be `apply true` at root.

**KMP/CMP consumer (berbudget-shaped):**

```kotlin
plugins {
    alias(sjy.plugins.android.application) apply false
    alias(sjy.plugins.android.library) apply false
    alias(sjy.plugins.compose.multiplatform) apply false
    alias(sjy.plugins.kotlin.compose) apply false
    alias(sjy.plugins.kotlin.multiplatform) apply false
    alias(sjy.plugins.ksp) apply false
    alias(sjy.plugins.detekt) apply true
    alias(sjy.plugins.ktorfit) apply false
    alias(sjy.plugins.kotlin.serialization) apply false
    alias(sjy.plugins.android.kotlin.multiplatform.library) apply false
    alias(sjy.plugins.android.lint) apply false
}
```

**Firebase Android app (rally-rank-shaped)** — also declare:

```kotlin
alias(sjy.plugins.gms.services) apply false
alias(sjy.plugins.crashlytics) apply false
```

Do **not** declare `sjy.plugins.kotlin.android`.

### Step 4: Shared modules (optional)

See [shared-modules.md](./shared-modules.md). Canonical KMP set used by berbudget + rally-rank-app:

- `:core:data-pref` + `:api` + `:impl`
- `:core:utils`
- `:core:presentation`

---

## 3. Module plugin scenarios

### A. Android application (`:androidApp`)

```kotlin
plugins {
    alias(sjy.plugins.buildlogic.app)
    alias(sjy.plugins.buildlogic.compose) // if Compose
    alias(sjy.plugins.buildlogic.firebase) // optional
    alias(sjy.plugins.buildlogic.detekt)
}
```

### B. Android library

```kotlin
plugins {
    alias(sjy.plugins.buildlogic.lib)
    alias(sjy.plugins.buildlogic.compose) // if Compose
    alias(sjy.plugins.buildlogic.detekt)
}
```

### C. KMP / CMP library (`:shared`, domain, core KMP)

```kotlin
plugins {
    alias(sjy.plugins.buildlogic.multiplatform.lib)
    alias(sjy.plugins.buildlogic.multiplatform.cmp) // shared UI
    alias(sjy.plugins.buildlogic.multiplatform.ios.app.version) // if this module feeds iosApp
    alias(sjy.plugins.buildlogic.detekt)
}
```

`:androidApp` stays a separate `buildlogic.app` module depending on the KMP library.

### D. JVM / MCP (raw IDs — no catalog aliases yet)

```kotlin
plugins {
    id("com.sanjaya.buildlogic.jvm.lib")
    id("com.sanjaya.buildlogic.jvm.koin") // optional
    id("com.sanjaya.buildlogic.jvm.serialization") // optional
    id("com.sanjaya.buildlogic.jvm.mcp.server") // MCP apps
    id("com.sanjaya.buildlogic.jvm.test") // optional
}
```

---

## 4. Verification smoke

```bash
./gradlew projects
./gradlew :androidApp:assembleDebug   # or consumer equivalent
./gradlew detekt
```

For KMP metadata: `./gradlew compileKotlinMetadata` / `compileCommonMainKotlinMetadata` when the consumer defines it.

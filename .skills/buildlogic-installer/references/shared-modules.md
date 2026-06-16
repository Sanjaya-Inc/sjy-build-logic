# Reusable Shared Modules Reference

`sjy-build-logic` bundles pre-built utility and data modules. Include them in target projects by setting their `projectDir` to their relative path inside the submodule.

---

## 1. Multiplatform Modules (KMP/CMP Projects)

Register these inside the target project's `settings.gradle.kts`:

### Registration DSL
```kotlin
include(":core:data-pref")
project(":core:data-pref").projectDir = file("sjy-build-logic/shared/multiplatform/data-pref")

include(":core:data-supabase")
project(":core:data-supabase").projectDir = file("sjy-build-logic/shared/multiplatform/data-supabase")

include(":core:utils")
project(":core:utils").projectDir = file("sjy-build-logic/shared/multiplatform/utils")

include(":core:presentation")
project(":core:presentation").projectDir = file("sjy-build-logic/shared/multiplatform/presentation")
```

### Module Descriptions
- **data-pref**: Key-Value preferences storage implementation wrapping multiplatform DataStore/MMKV.
- **data-supabase**: Multiplatform Supabase Client wrappers and authentication utilities.
- **utils**: Core Kotlin common utilities (datetime extensions, formatting helpers).
- **presentation**: Presentation components and Orbit MVI/ViewModel boilerplate configurations.

---

## 2. Android-Specific Modules (Android-Only Projects)

Register these inside the target project's `settings.gradle.kts`:

### Registration DSL
```kotlin
include(":core:android-data-local")
project(":core:android-data-local").projectDir = file("sjy-build-logic/shared/android/data/local")

include(":core:android-data-network")
project(":core:android-data-network").projectDir = file("sjy-build-logic/shared/android/data/network")

include(":core:android-data-pref")
project(":core:android-data-pref").projectDir = file("sjy-build-logic/shared/android/data/pref")

include(":core:android-utils")
project(":core:android-utils").projectDir = file("sjy-build-logic/shared/android/utils")
```

### Module Descriptions
- **data-local**: Android SQLite and Room database configuration wrappers.
- **data-network**: Android network client wrappers using OkHttp/Retrofit/Ktorfit.
- **data-pref**: Android-specific SharedPreferences and DataStore configurations.
- **utils**: Android Context, permissions, and device compatibility utilities.

---

## 3. Usage in Downstream Modules

Add the registered module dependency directly inside your client module's `build.gradle.kts` (e.g., `:shared` or `:androidApp`):
```kotlin
dependencies {
    implementation(projects.core.utils)
    implementation(projects.core.dataPref)
}
```

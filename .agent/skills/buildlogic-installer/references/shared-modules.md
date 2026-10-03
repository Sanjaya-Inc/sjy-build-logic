# Shared Modules Reference

Remap submodule sources into the consumer Gradle graph with `projectDir`. Enable `TYPESAFE_PROJECT_ACCESSORS` for `projects.core.*` accessors.

Path root: `sjy-build-logic/shared/`.

---

## 1. Canonical KMP set (berbudget + rally-rank-app)

### Registration DSL

```kotlin
include(":core")

include(":core:data-pref:api")
project(":core:data-pref:api").projectDir =
    file("sjy-build-logic/shared/multiplatform/data-pref/api")
include(":core:data-pref:impl")
project(":core:data-pref:impl").projectDir =
    file("sjy-build-logic/shared/multiplatform/data-pref/impl")
include(":core:data-pref")
project(":core:data-pref").projectDir =
    file("sjy-build-logic/shared/multiplatform/data-pref")

include(":core:utils")
project(":core:utils").projectDir =
    file("sjy-build-logic/shared/multiplatform/utils")

include(":core:presentation")
project(":core:presentation").projectDir =
    file("sjy-build-logic/shared/multiplatform/presentation")
```

### Roles

| Include | Path | Consumer dep | Role |
| :--- | :--- | :--- | :--- |
| `:core:data-pref:api` | `multiplatform/data-pref/api` | `projects.core.dataPref.api` | Pref interfaces / `@SecuredPreferences` |
| `:core:data-pref:impl` | `multiplatform/data-pref/impl` | via facade | DataStore + secured repo + `DataPrefModule` |
| `:core:data-pref` | `multiplatform/data-pref` | `projects.core.dataPref` | Facade: `api(api)` + `implementation(impl)` + `dataPrefModule` |
| `:core:utils` | `multiplatform/utils` | `projects.core.utils` | Platform, auth, permissions, media, jobs |
| `:core:presentation` | `multiplatform/presentation` | `projects.core.presentation` | Navigation3, deeplinks, snackbar, BaseViewModel |

Default app/shared dependency: facade `projects.core.dataPref` (not impl). Compile-boundary modules may take `projects.core.dataPref.api` only.

---

## 2. Optional multiplatform modules

### data-supabase

```kotlin
include(":core:data-supabase")
project(":core:data-supabase").projectDir =
    file("sjy-build-logic/shared/multiplatform/data-supabase")
```

Dep: `implementation(projects.core.dataSupabase)`.

### glass-ui

```kotlin
include(":core:presentation:glass-ui")
project(":core:presentation:glass-ui").projectDir =
    file("sjy-build-logic/shared/multiplatform/presentation/glass-ui")
```

Dep: `implementation(projects.core.presentation.glassUi)`.

Neither berbudget nor rally-rank-app includes these today; still valid optional cores.

---

## 3. Optional Android-only modules

```kotlin
include(":core:android-data-local")
project(":core:android-data-local").projectDir =
    file("sjy-build-logic/shared/android/data/local")

include(":core:android-data-network")
project(":core:android-data-network").projectDir =
    file("sjy-build-logic/shared/android/data/network")

include(":core:android-data-pref")
project(":core:android-data-pref").projectDir =
    file("sjy-build-logic/shared/android/data/pref")

include(":core:android-utils")
project(":core:android-utils").projectDir =
    file("sjy-build-logic/shared/android/utils")
```

| Include | Role |
| :--- | :--- |
| `android-data-local` | SQLCipher `DatabaseFactory` + Startup initializer |
| `android-data-network` | OkHttp / Ktorfit creators + SSL pins |
| `android-data-pref` | `SecureDataStoreSerializer` for typed encrypted DataStore |
| `android-utils` | `CipherUtils`, `DispatcherProvider`, `KoinInitializer` |

Note: rally-rank networking is **app-local** `:core:network:{api,impl,testing}` — do not assume sjy `android-data-network` is wired.

---

## 4. Downstream usage

```kotlin
dependencies {
    implementation(projects.core.utils)
    implementation(projects.core.presentation)
    implementation(projects.core.dataPref)
}
```

Public APIs: [shared-apis.md](./shared-apis.md).

App-owned modules stay in the consumer (`:core:design-system`, domain triads, local network). Do not move product types into sjy shared cores.

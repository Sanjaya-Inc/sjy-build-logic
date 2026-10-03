# Convention Plugins Reference

Precompiled script plugins under `convention/{android,multiplatform,tools,jvm}`. Public consumers use `alias(sjy.plugins.buildlogic.*)` from the injected `sjy` catalog.

---

## Catalog aliases (`gradle/libs.versions.toml`)

| Catalog key | Accessor | Plugin ID |
| :--- | :--- | :--- |
| `buildlogic-app` | `sjy.plugins.buildlogic.app` | `com.sanjaya.buildlogic.app` |
| `buildlogic-lib` | `sjy.plugins.buildlogic.lib` | `com.sanjaya.buildlogic.lib` |
| `buildlogic-compose` | `sjy.plugins.buildlogic.compose` | `com.sanjaya.buildlogic.compose` |
| `buildlogic-firebase` | `sjy.plugins.buildlogic.firebase` | `com.sanjaya.buildlogic.firebase` |
| `buildlogic-test` | `sjy.plugins.buildlogic.test` | `com.sanjaya.buildlogic.test` |
| `buildlogic-detekt` | `sjy.plugins.buildlogic.detekt` | `com.sanjaya.buildlogic.detekt` |
| `buildlogic-multiplatform-lib` | `sjy.plugins.buildlogic.multiplatform.lib` | `com.sanjaya.buildlogic.multiplatform.lib` |
| `buildlogic-multiplatform-cmp` | `sjy.plugins.buildlogic.multiplatform.cmp` | `com.sanjaya.buildlogic.multiplatform.cmp` |
| `buildlogic-multiplatform-ios-app-version` | `sjy.plugins.buildlogic.multiplatform.ios.app.version` | `com.sanjaya.buildlogic.multiplatform.ios-app-version` |

Internal (no catalog alias; auto-applied): `com.sanjaya.buildlogic.common`, `com.sanjaya.buildlogic.target`.

---

## Android (`convention/android`)

| Plugin | Apply when | Notes |
| :--- | :--- | :--- |
| `buildlogic.app` | `:androidApp` / Android application | Applies `com.android.application` + `common`. Release: `optimization.enable = true`, `ndk.debugSymbolLevel = "FULL"`. |
| `buildlogic.lib` | Android library | Applies `com.android.library` + `common`; consumer ProGuard. |
| `buildlogic.compose` | App/lib with Jetpack Compose | BOM, destinations KSP, optional compiler reports. Stack on app/lib. |
| `buildlogic.firebase` | App needing GMS/Crashlytics + Firebase BOM | Requires root `gms.services` / `crashlytics` `apply false`. |
| `buildlogic.test` | JUnit4/5 + MockK + Turbine + Jacoco unit reports | Optional; not required by current berbudget/rally-rank modules. |
| `common` / `target` | **Do not apply in consumers** | `app`/`lib` → `common` → `target`. `target` sets SDK/Java from `compile-sdk` / `min-sdk` / `jvm-target`. |

---

## Kotlin Multiplatform (`convention/multiplatform`)

| Plugin | Apply when | Notes |
| :--- | :--- | :--- |
| `buildlogic.multiplatform.lib` | Every KMP library | Applies `kotlin-multiplatform` + `android-kotlin-multiplatform-library`, KSP, serialization, ktorfit, koin-compiler; Android + iOS arm64/sim frameworks; common deps (koin, coroutines, immutable, datetime, store, ktor*). |
| `buildlogic.multiplatform.cmp` | Shared Compose UI | Stack on multipatform.lib. CMP + material3 + Orbit + lifecycle + Navigation3. |
| `buildlogic.multiplatform.ios.app.version` | Module that owns iOS framework versioning | Writes `iosApp/Configuration/Version.xcconfig` from consumer `app-version-name` / `app-version-code` via `SyncIosAppVersionTask`. |

AGP 9+: KMP modules are libraries only. Host Android UI in `:androidApp`.

---

## Tools (`convention/tools`)

| Plugin | Apply when | Notes |
| :--- | :--- | :--- |
| `buildlogic.detekt` | Almost every module | Shared `config/detekt-rule.yml` + formatting/compose rules. |

---

## JVM (`convention/jvm`) — source-ready; no catalog aliases yet

| Plugin ID | Apply when | Notes |
| :--- | :--- | :--- |
| `com.sanjaya.buildlogic.jvm.lib` | Pure Kotlin JVM lib | Kotlin JVM + detekt + Java 21 toolchain. |
| `com.sanjaya.buildlogic.jvm.koin` | JVM + Koin | Depends on jvm.lib + koin-compiler. |
| `com.sanjaya.buildlogic.jvm.serialization` | JVM + kotlinx.serialization | Depends on jvm.lib. |
| `com.sanjaya.buildlogic.jvm.mcp.server` | MCP server apps | Depends on jvm.koin + `application`; mcp-sdk, logback. |
| `com.sanjaya.buildlogic.jvm.test` | JVM tests | kotlin-test / junit. |

Use raw `id("...")` until aliases are added to `[plugins]`.

---

## Core helpers (`convention/core`)

When authoring or extending convention plugins, reuse:

| API | Purpose |
| :--- | :--- |
| `Project.sjyVersion(alias)` | Version from `sjy`, fallback `libs` |
| `Project.sjyLibrary(alias)` | Library notation from `sjy`, fallback `libs` |
| `Project.sjyBundle(alias)` | Bundle from `sjy`, fallback `libs` |
| `Project.sjyPlugin(alias)` | Plugin id from `sjy`, fallback `libs` |
| `SyncIosAppVersionTask` | Cacheable iOS xcconfig sync |

Never hardcode dependency coordinates inside convention plugins when a catalog alias exists.

---

## Consumer ground truth

| Consumer | Module | Plugins |
| :--- | :--- | :--- |
| berbudget | `:androidApp` | app + compose + detekt |
| berbudget | `:shared`, design-system, cores | multipatform.lib + cmp + detekt |
| rally-rank-app | `:androidApp` | app + compose + detekt + firebase |
| rally-rank-app | `:shared` | multipatform.lib + cmp + **ios.app.version** + detekt |
| rally-rank-app | domain/core KMP | multipatform.lib (+ cmp / buildconfig as needed) + detekt |

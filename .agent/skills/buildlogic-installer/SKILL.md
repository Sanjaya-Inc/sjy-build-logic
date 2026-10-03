---
name: buildlogic-installer
description: "Use when installing, linking, or configuring sjy-build-logic convention plugins or shared modules (data-pref, utils, presentation, data-supabase, glass-ui, android data/utils) into an Android or Kotlin/Compose Multiplatform project; also when applying buildlogic.* plugins, includeBuild wiring, or reusing shared core APIs."
---

# Buildlogic Installer

Composite `includeBuild` conventions + remappable `:core:*` shared modules. Prefer existing shared types over reimplementing auth, prefs, navigation, deeplinks, permissions, or media helpers.

<instructions>
Read the decision matrix, then open only the reference needed. Use relative paths under `./references/`.
</instructions>

## 1. Quick Decision Matrix

| Task / Context | Action | Reference |
| :--- | :--- | :--- |
| New consumer project setup | Submodule + `includeBuild` + `sjy` catalog + root aliases | [installation.md](./references/installation.md) |
| Which convention plugin? | Apply catalog alias; never redefine plugin IDs in consumer `libs.versions.toml` | [plugins.md](./references/plugins.md) |
| Android app module | `buildlogic.app` (+ `compose`, optional `firebase`) + `detekt` | [plugins.md §Android](./references/plugins.md) |
| Android library module | `buildlogic.lib` (+ `compose`) + `detekt` | [plugins.md §Android](./references/plugins.md) |
| KMP / CMP shared or domain lib | `buildlogic.multiplatform.lib` (+ `cmp`) + `detekt` | [plugins.md §KMP](./references/plugins.md) |
| iOS version sync from catalog | `buildlogic.multiplatform.ios.app.version` on the KMP module that owns the iOS framework | [plugins.md §KMP](./references/plugins.md) |
| JVM / MCP server module | Raw IDs `com.sanjaya.buildlogic.jvm.*` (no catalog aliases yet) | [plugins.md §JVM](./references/plugins.md) |
| Link shared KMP cores | Remap `projectDir` under `:core:*`; data-pref is api/impl/facade triad | [shared-modules.md](./references/shared-modules.md) |
| Prefs / secured storage | `PreferenceRepository`, `dataPrefModule`, `createSecuredRepository`, `@SecuredPreferences` | [shared-apis.md §data-pref](./references/shared-apis.md) |
| Navigation / deeplinks / snackbar | `NavigationRouter`, deeplink stack, `corePresentationModule` | [shared-apis.md §presentation](./references/shared-apis.md) |
| Permissions / photo / share / SSO | `AppPermission`, `PhotoPickerController`, `FirebaseSocialAuthUseCase`, `SkipIfRunningJob` | [shared-apis.md §utils](./references/shared-apis.md) |
| Glass UI surfaces | Optional `:core:presentation:glass-ui` — `GlassContainer`, `GlassTheme` | [shared-modules.md](./references/shared-modules.md), [shared-apis.md §glass-ui](./references/shared-apis.md) |
| Supabase auth client | Optional `:core:data-supabase` | [shared-modules.md](./references/shared-modules.md) |
| Android-only Room/OkHttp cores | Optional `shared/android/*` remaps | [shared-modules.md §Android](./references/shared-modules.md) |
| Extend convention plugins | Use `sjyVersion` / `sjyLibrary` / `sjyPlugin` from `convention/core` | [plugins.md §Core helpers](./references/plugins.md) |

## 2. References

<references>
- [installation.md](./references/installation.md) — baselines, submodule, settings, root plugins, consumer scenarios
- [plugins.md](./references/plugins.md) — plugin IDs, catalog aliases, apply rules, internals
- [shared-modules.md](./references/shared-modules.md) — `settings.gradle.kts` include DSL + dependency accessors
- [shared-apis.md](./references/shared-apis.md) — public classes/functions agents MUST reuse
</references>

## 3. Constraints

<constraints>
- NEVER copy or redefine sjy plugin IDs / version pins into the consumer `gradle/libs.versions.toml`. Inject catalog via `versionCatalogs.create("sjy") { from(files("sjy-build-logic/gradle/libs.versions.toml")) }`.
- ALWAYS apply conventions with `alias(sjy.plugins.buildlogic.*)` when a catalog alias exists.
- `includeBuild("sjy-build-logic")` MUST be first inside root `pluginManagement`.
- KMP modules under AGP 9+ are libraries only (`com.android.kotlin.multiplatform.library`). Apps live in a separate `:androidApp` applying `buildlogic.app`.
- NEVER apply `org.jetbrains.kotlin.android` on Android modules — AGP 9 built-in Kotlin.
- data-pref MUST register api + impl + facade (not a single flat include).
- Prefer shared APIs in [shared-apis.md](./references/shared-apis.md) over new app-local duplicates for prefs, navigation, deeplinks, permissions, media, social auth, and job helpers.
- App-specific types stay in the consumer (design-system, domain, network). Do not push product types into sjy `:core:utils` / `:core:presentation`.
- `common` / `target` are internal — applied by app/lib; do not apply them directly in consumers.
- Skill lives at `.agent/skills/buildlogic-installer/` inside this repo (not host-app `.skills/` / `.agents/skills/` trees).
</constraints>

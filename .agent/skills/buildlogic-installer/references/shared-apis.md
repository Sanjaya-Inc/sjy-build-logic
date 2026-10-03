# Shared Public APIs

Reuse these types instead of reimplementing. Packages are under each remapped `:core:*` module.

---

## `:core:utils` (`core.utils*`)

| Symbol | Purpose |
| :--- | :--- |
| `Platform` / `PlatformContext` | expect/actual platform identity + DI context |
| `coreUtilsModule(platformContext)` / `CoreUtilsModule` | Koin scan of `core.utils` |
| `SkipIfRunningJob` | Mutex-gated launch; skips overlapping work |
| `DebouncedJob` / `SjyDispatchers` | Cancel+delay submit; injectable Main/IO/Default |
| `FirebaseSocialAuthUseCase` / `rememberFirebaseSocialAuthUseCase` | Google/Apple → `FirebaseAuthCredential` |
| `FirebaseAuthProvider` / `FirebaseAuthCredential` / `FirebaseSocialAuthError` | SSO models + errors |
| `AppPermission` / `PermissionRequestStrategy` / `PermissionRequesterRegistry` | Sealed permissions + strategy registry |
| `RequestPermission` / `rememberPermissionRequest` | Compose permission UX |
| `NotificationPermissionStrategy` | expect/actual notifications strategy |
| `PhotoPickerController` / `rememberPhotoPicker` | Gallery/camera → `ByteArray` |
| `ShareImageCapture` / `rememberShareImageCapture` | Capture composable bounds |
| `ShareMatchCompositor` / `compositeMatchShareImage` / `ShareMatchCompositeRequest` | Match-share compositing |

DI: call `coreUtilsModule(platformContext)` from app Koin start.

---

## `:core:data-pref` triad (`core.pref*`)

### api

| Symbol | Purpose |
| :--- | :--- |
| `PreferenceWriter` | suspend put/remove/clear |
| `PreferenceReader` | Flow getters (string/bool/int/long) |
| `PreferenceRepository` | Writer + Reader — default depend-on type |
| `@SecuredPreferences` | Koin `@Named` qualifier for encrypted binding |

### impl / facade

| Symbol | Purpose |
| :--- | :--- |
| `PreferenceRepositoryImpl` | DataStore-backed repository |
| `createDataStore(name)` | expect/actual DataStore factory |
| `createSecuredRepository()` | Keystore/Keychain secured `PreferenceRepository` |
| `SecuredPreferencesRepository` | `@Single` `@SecuredPreferences` binding |
| `dataPrefModule` / `DataPrefModule` | Public Koin module entry |

There is **no** `SecureStorage` type — use `createSecuredRepository` / `@SecuredPreferences`.

---

## `:core:presentation` (`core.presentation*`)

| Symbol | Purpose |
| :--- | :--- |
| `Route` | Navigation3 destination marker |
| `NavigationRouter` | `backStack` StateFlow; `navigateTo` / `replaceTop` / `onBack` / `clearBackStack` |
| `rememberNavigationRouter` / `LocalNavigationRouter` | Compose router host/access |
| `NavigationTransitions` / `TransitionSpecs` | NavDisplay transition presets |
| `DeepLinkUri` / `DeepLinkConfig` / `DeepLinkParser` / `DeepLinkResolver` | Parse + resolve |
| `DeepLinkDestination` (`Navigation` \| `Unhandled`) | Resolved target |
| `DeepLinkHandler` / `DeepLinkNavigator` | Handle + navigate |
| `DeepLinkBridge` / `DeepLinkNestedBridge` | Pending request/consume handoff |
| `BaseViewModel` / `ScopedFeatures` | Shared VM helpers |
| `SnackbarEventBus` | Cross-feature snackbar events |
| `corePresentationModule` / `CorePresentationModule` | Koin scan `core.presentation` |

Not Orbit boilerplate — Navigation3 + deeplink + snackbar.

---

## `:core:presentation:glass-ui` (`core.presentation.glass`)

| Symbol | Purpose |
| :--- | :--- |
| `GlassContainer` / `GlassContainerWithShadow` / `GlassContainerWithDepth` | Glass surfaces |
| `GlassCard` / `GlassButton` (+ shadow variants) | Glass components |
| `GlassConfig` / `GlassTheme` / `LocalGlassTheme` | Tokens + theme |
| `glassDropShadow` / `glassGloss` / `glassNoiseEffect` | Modifiers |

---

## `:core:data-supabase` (`core.supabase*`)

| Symbol | Purpose |
| :--- | :--- |
| `SupabaseClient` | Client wrapper |
| `SupabaseAuthRepo` / `SupabaseAuthRepoImpl` | Email/OAuth/reset/signOut + auth state |
| `SupabaseUser` / `SupabaseError` / `EmailCredentials` / `AuthProvider` | Domain models |
| `SupabaseInitUseCase` | Bootstrap |
| `SupabaseModule` | Koin `@Module` `@ComponentScan("core.supabase")` |

---

## Android-only (`shared/android/*`)

| Module | Symbols |
| :--- | :--- |
| `android-data-local` | `DatabaseFactory`, `DatabasePassphrasePref`, `CoreLocalModule`, `CoreLocalModuleInitializer` |
| `android-data-network` | `BaseUrlProvider`, `SslPinsProvider`, `OkhttpClientCreator`, `KtorfitCreator`, `CoreNetworkModule`, `CoreNetworkInitializer` |
| `android-data-pref` | `SecureDataStoreSerializer<T>` |
| `android-utils` | `CipherUtils`, `DispatcherProvider`, `StandardDispatcherProvider`, `CoreUtilsModule`, `KoinInitializer` |

---

## Anti-patterns

| Wrong | Right |
| :--- | :--- |
| New prefs wrapper in the app | `PreferenceRepository` + `dataPrefModule` |
| Custom nav backstack helper | `NavigationRouter` (`replaceTop` included) |
| Ad-hoc deeplink parsing | `DeepLinkParser` / `DeepLinkResolver` / `DeepLinkHandler` |
| One-off permission code | `AppPermission` + strategy + `RequestPermission` |
| Duplicate photo/share capture | `PhotoPickerController` / `ShareImageCapture` / compositor |
| Reimplement Google/Apple SSO glue | `FirebaseSocialAuthUseCase` |
| Put product models into sjy cores | Keep in consumer domain / design-system |

# 🍳 Kitchen Stock Manager (KitchenStock)

A Kotlin Multiplatform (KMP) application for managing kitchen inventory, tracking expiration dates, and keeping a synced shopping list. Built with an **Offline-First Architecture** — fast local reads/writes with background sync to the cloud.

This is the active codebase for the Kitchen Stock Manager product (superseding the earlier `Kiser/` scaffold, which was never developed further).

## 🏗 Tech Stack & Libraries

Business logic, data layer, and UI are shared across platforms via Kotlin and Compose Multiplatform.

### Core Libraries
* **[Koin](https://insert-koin.io/):** Dependency injection for view models, repositories, and platform clients.
* **[SQLDelight](https://cashapp.github.io/sqldelight/):** Local database (single source of truth). Typesafe Kotlin APIs generated from `.sq` files.
* **[Supabase](https://supabase.com/docs/reference/kotlin/introduction):** Backend-as-a-Service. `auth-kt` for authentication (email/password, Google OAuth, password reset), `postgrest-kt` for Postgres sync, `realtime-kt` for live updates.
* **[Coil (Coil3)](https://coil-kt.github.io/coil/upgrading_to_coil3/):** Multiplatform image loading.
* **[BuildKonfig](https://github.com/yshrsmz/BuildKonfig):** Generates a multiplatform `BuildConfig` for Supabase URL/keys from `gradle.properties`.
* **[Compose Navigation 3](https://kotlinlang.org/docs/multiplatform/compose-navigation-3.html):** Navigation for Compose Multiplatform.

### Additional KMP Essentials
* **UI Framework:** Compose Multiplatform (shared UI, not just shared logic) across all four targets.
* **Networking:** Ktor Client (also the underlying engine for the Supabase SDK).
* **Concurrency:** Kotlinx Coroutines & `StateFlow`.
* **Date & Time:** `kotlinx-datetime`.
* **Logging:** Napier.

## 📂 Project Structure

```text
├── androidApp/               # Android entry point (MainActivity, manifest, OAuth deep-link handling)
├── iosApp/                   # iOS entry point (Xcode workspace)
├── desktopApp/                # Desktop (JVM) entry point
├── webApp/                   # Web entry point (Wasm + JS targets)
└── shared/                   # Core business logic, data layer, and Compose UI
    ├── src/commonMain/        # SQLDelight schema, Supabase setup, Koin modules, ViewModels, Compose screens
    ├── src/androidMain/       # Android-specific drivers (SQLDelight, OAuth deeplink support)
    ├── src/iosMain/           # iOS-specific drivers
    ├── src/jvmMain/           # Desktop-specific drivers
    ├── src/jsMain/            # JS-specific drivers
    └── src/wasmJsMain/        # Wasm-specific drivers
```

## 📐 Architecture

Clean Architecture + MVI. Every screen is split into a **Screen** (stateful, wires `ViewModel` + navigation) and a **Content** (stateless, pure UI, always previewable in light/dark) — see `.agents/AGENTS.md` for the full convention.

* **Presentation:** Compose Multiplatform, `ViewModel`s expose `StateFlow<UiState>`.
* **Domain:** Use cases + repository interfaces, no platform dependencies.
* **Data:** Repositories mediate between SQLDelight (local) and Supabase (remote).

### Offline-First Sync
* **Local is king:** all UI reads/writes go through SQLDelight first.
* **Sync:** repositories pull from Supabase (`postgrest`) and replace local tables; writes are pushed to Supabase right after the local write.
* **Auth:** Supabase Auth session persisted via `multiplatform-settings`; Google Sign-In uses the OAuth browser-redirect flow (see below), not native Credential Manager.

## 🔑 Auth Features

* Email/password sign up, login, logout.
* **Sign in with Google** — OAuth flow via external browser + deep link callback (`kitchenstock://login-callback`, registered in `AndroidManifest.xml`, handled in `MainActivity`). Requires the Google provider to be enabled with a Client ID/Secret in the Supabase project dashboard (Authentication → Providers) — this is a one-time manual step outside this repo, see `.agents/SUPABASE_SETUP.md` for the full checklist.
* **Forgot password** — sends a reset link via `auth.resetPasswordForEmail`.
* Sign in with Apple, anonymous sign-in: wired in `AuthRepository`, same OAuth pattern as Google.
* Not yet implemented: change password while logged in (planned, see `.agents/docs/PRD.md` in the workspace root).

## 🛠 Development Setup

### Prerequisites
* **OS:** macOS required to build/run the iOS app. Android, Desktop, and Web targets build fine on Linux.
* **IDE:** Android Studio (latest stable) with the Kotlin Multiplatform plugin, or IntelliJ IDEA.
* **iOS IDE:** Xcode, for the iOS simulator/build.
* **Java:** JDK 17+.

### Running the apps
* **Android:** `./gradlew :androidApp:assembleDebug` (or the `androidApp` run configuration).
* **Desktop:** `./gradlew :desktopApp:run` (hot reload: `./gradlew :desktopApp:hotRun --auto`).
* **Web:** `./gradlew :webApp:wasmJsBrowserDevelopmentRun` (or `:webApp:jsBrowserDevelopmentRun` for older browsers).
* **iOS:** open `iosApp/iosApp.xcworkspace` in Xcode and run from there.

## 🧪 Testing

* Shared logic: `./gradlew :shared:allTests`
* Android host tests: `./gradlew :shared:testAndroidHostTest`
* Desktop tests: `./gradlew :shared:jvmTest`
* Web tests: `./gradlew :shared:wasmJsTest` / `:shared:jsTest`
* iOS tests: `./gradlew :shared:iosSimulatorArm64Test` (macOS only)

---

See `.agents/AGENTS.md` for coding conventions and `.agents/DESIGN.md` for the visual design system ("Culinary Clarity"). Product-level docs (PRD/TRD/DESIGN, open product questions) live in the workspace root at `../.agents/docs/`.

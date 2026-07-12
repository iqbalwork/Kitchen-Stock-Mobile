# Implementation Plan: Migrating KitchenStockMobile to Multi-Platform Support

This plan outlines the steps to migrate the existing `KitchenStockMobile` project into the `KitchenStock` multi-platform template, enabling support for Desktop and Web in addition to Android and iOS.

## Phase 1: Preparation & Analysis
1.  **Dependency Alignment**: Compare `build.gradle.kts` (root and shared) between `KitchenStockMobile` and `KitchenStock`.
    *   Add missing dependencies to `KitchenStock`: Koin, SQLDelight, Coroutines, etc.
    *   Ensure Compose Multiplatform versions are compatible across all targets.
2.  **Package Strategy**: Decide whether to rename `com.iqbalfauzi.kitchenstockmobile` to `com.iqbalfauzi.kitchenstock` or keep it. Renaming is preferred for consistency.

## Phase 2: Core Logic Migration
1.  **Shared Module Migration**:
    *   Copy `data`, `domain`, and `di` packages from `KitchenStockMobile/shared/src/commonMain` to `KitchenStock/shared/src/commonMain`.
    *   Migrate SQLDelight `.sq` files.
2.  **Platform-Specific Logic**:
    *   Migrate `DbDriverFactory` for Android and iOS.
    *   **New**: Implement `DbDriverFactory` for `jvmMain` (Desktop) and `wasmJsMain` (Web).
    *   Update `Platform.kt` for Desktop and Web.

## Phase 3: UI Migration
1.  **Theme and Components**:
    *   Copy `ui/theme` and common UI components.
2.  **Presentation Logic**:
    *   Copy `presentation` package (ViewModels and Screens) to `commonMain`.
    *   Ensure all `Composable` functions use multi-platform compatible APIs.
3.  **Navigation**:
    *   Migrate navigation logic to `commonMain` using a multi-platform navigation library (already present in `KitchenStockMobile`).

## Phase 4: Platform Integration
1.  **Android & iOS**: Update entry points to point to the migrated `App.kt`.
2.  **Desktop**:
    *   Configure `desktopApp/src/jvmMain` to initialize Koin and SQLDelight.
    *   Update `main.kt` to launch the shared `App`.
3.  **Web (Wasm)**:
    *   Configure `webApp/src/wasmJsMain` to initialize Koin and SQLDelight (using `Worker` or IndexedDB driver for SQLDelight if needed).
    *   Update `main.kt` to launch the shared `App`.

## Phase 5: Verification & Testing
1.  **Build & Run**:
    *   Verify Android build.
    *   Verify Desktop build (`./gradlew :desktopApp:run`).
    *   Verify Web build (`./gradlew :webApp:wasmJsBrowserDevelopmentRun`).
    *   Verify iOS build.
2.  **Fix Platform Issues**: Address UI layout issues on different screen sizes (especially Desktop/Web vs Mobile).

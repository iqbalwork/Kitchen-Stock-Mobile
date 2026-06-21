# 🍳 Kitchen Stock Manager (KitchenStockMobile)

A Kotlin Multiplatform (KMP) mobile application built to efficiently manage kitchen inventory, track expiration dates, and automate shopping lists. This project utilizes an **Offline-First Architecture**, ensuring lightning-fast local interactions with seamless cloud synchronization in the background.

## 🏗 Tech Stack & Libraries

This project shares its core business logic, data layer, and UI state across platforms using Kotlin, while maintaining native performance.

### Core Libraries
* **[Koin](https://insert-koin.io/):** Dependency Injection framework designed for Kotlin, used to provide view models, repositories, and local/remote clients across the shared module.
* **[SQLDelight](https://cashapp.github.io/sqldelight/):** Local Database (Single Source of Truth). Generates type-safe Kotlin APIs from raw SQL statements for blazing-fast local operations.
* **[Supabase](https://supabase.com/docs/reference/kotlin/introduction):** Backend-as-a-Service for cloud synchronization. Utilizes `gotrue-kt` for family account authentication and `postgrest-kt` for real-time database sync.
* **[Coil (Coil3)](https://coil-kt.github.io/coil/upgrading_to_coil3/):** Image loading library fully compatible with Kotlin Multiplatform, used for rendering ingredient and recipe images efficiently.
* **[Wiretap](https://github.com/skymansandy/wiretapKMP):** Network debugging and logging interceptor to monitor background sync payloads and API calls.
* **[BuildKonfig](https://github.com/yshrsmz/BuildKonfig):** A Gradle plugin that generates a `BuildConfig` class for Kotlin Multiplatform projects, allowing platform-specific configurations like API keys and base URLs to be shared across targets.
* **[Compose Navigation 3](https://kotlinlang.org/docs/multiplatform/compose-navigation-3.html):** Jetpack Navigation 3 for Compose Multiplatform.

### Additional KMP Essentials
* **UI Framework:** Compose Multiplatform (CMP) for shared UI elements.
* **Networking:** Ktor Client for HTTP requests (Recipe APIs and Supabase underlying engine).
* **Concurrency:** Kotlinx Coroutines & `StateFlow` for reactive state management and background syncing.
* **Date & Time:** `kotlinx-datetime` for handling expiry date calculations and sync timestamps across different time zones.

## 📂 Project Structure

Based on the standard Kotlin Multiplatform structure:

```text
├── androidApp/              # Android application entry point (MainActivity, Theme setup)
├── iosApp/                  # iOS application entry point (Xcode workspace, SwiftUI host)
└── shared/                  # Core Business Logic & Shared Data Layer
    ├── src/commonMain/      # SQLDelight queries, Supabase setup, Koin modules, ViewModels
    ├── src/androidMain/     # Android-specific implementations (e.g., AndroidSqliteDriver)
    └── src/iosMain/         # iOS-specific implementations (e.g., NativeSqliteDriver)
```

## 📐 Architecture

This project follows the **MVVM / MVI** pattern within the Presentation layer, strictly separating concerns to maximize code sharing in the `shared` module.

### 1. The Layers
* **Presentation Layer (UI & ViewModels):** Built with Compose Multiplatform. ViewModels hold the UI state using `StateFlow` and process user intents.
* **Domain Layer (Use Cases):** Contains the core business logic (e.g., `CalculateExpiryDateUseCase`, `GenerateShoppingListUseCase`).
* **Data Layer (Repositories):** Manages data retrieval and storage, acting as the mediator between the local database and the remote cloud backend.

### 2. Offline-First Sync Strategy
To provide a zero-latency user experience and enable multi-device synchronization (e.g., family sharing):
* **Local is King:** All UI read/write operations execute directly against the **SQLDelight** local database.
* **Background Syncing:** A dedicated `SyncWorker` listens to local database changes and pushes mutations to **Supabase** in the background.
* **Conflict Resolution:** If multiple devices update the same ingredient, the system relies on Supabase's `updated_at` timestamps to determine the ultimate source of truth, gracefully pulling the latest state back into the SQLDelight database.

---

## 🛠 Development Setup

To build and run this project, ensure your environment meets the following requirements.

### Prerequisites
* **OS:** macOS is strictly required for compiling the iOS app. An Apple Silicon (M-series) machine is highly recommended to handle Compose Multiplatform indexing and Xcode builds smoothly without freezing. *(Note: Linux, such as Fedora/KDE, can be used if you are strictly building the Android target).*
* **IDE:** Android Studio (latest stable release) with the official **Kotlin Multiplatform** plugin installed.
* **iOS IDE:** Xcode (latest version) for iOS simulator and build tools.
* **Java:** JDK 17 or higher.

### Running the Apps
* **Android:** Select the `androidApp` run configuration from the Android Studio toolbar, choose an emulator or physical device, and hit **Run** (or execute `./gradlew :androidApp:assembleDebug`).
* **iOS:** Open `iosApp/iosApp.xcworkspace` in Xcode, select your preferred iPhone simulator, and hit **Run**. Alternatively, use the KMP run configurations directly inside Android Studio.

---

## 🧪 Testing

Testing is prioritized in the shared module to ensure business logic validity across both platforms.

* **Shared Logic Tests:** Unit tests for ViewModels, UseCases, and Repositories are located in `shared/src/commonTest/`. Run them using your IDE gutter icons or via terminal:
  ```bash
  ./gradlew :shared:allTests

* **Platform-Specific Tests:**

* Android integration/DB tests: 
```bash
./gradlew :shared:testAndroidHostTest
```
* iOS logic tests: 
```bash
./gradlew :shared:iosSimulatorArm64Test
```

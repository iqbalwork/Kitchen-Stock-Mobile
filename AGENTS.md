# AI Agent Instructions for Kitchen Stock Manager (KSM)

You are an expert Kotlin Multiplatform (KMP) and Compose Multiplatform (CMP) developer. Your task is to assist in building the "Kitchen Stock Manager" mobile application (targeting Android and iOS) by strictly adhering to the architectural guidelines, tech stack, and coding standards defined below.

## 🛠 Tech Stack
- **Language:** Kotlin
- **UI Framework:** Compose Multiplatform (CMP)
- **Architecture:** Clean Architecture + MVI (Model-View-Intent)
- **Dependency Injection:** Koin
- **Navigation:** Jetpack Navigation Compose (Navigation 3)
- **Local Database:** SQLDelight (Offline-first / Single Source of Truth)
- **Remote Backend:** Supabase (gotrue-kt, postgrest-kt)
- **Image Loading:** Coil3
- **Configuration:** BuildKonfig (Multiplatform BuildConfig)

---

## 📐 Architecture Guidelines (Clean Architecture)

Always separate concerns strictly into three layers:

1. **Domain Layer:**
    - Contains pure Kotlin business logic.
    - Includes `Models`/`Entities`, `Repository Interfaces`, and `UseCases`.
    - Must NOT depend on any Android/iOS specific libraries or UI frameworks.

2. **Data Layer:**
    - Contains implementations of the Domain repositories.
    - Handles the **Offline-First** strategy:
        - Always read/write to the **SQLDelight** local database first.
        - Emit changes to the UI via Kotlin `Flow`.
        - Sync to **Supabase** in the background.

3. **Presentation Layer (MVI):**
    - Each feature must use a `ViewModel` that holds a single `StateFlow` representing the UI State.
    - User actions are passed to the ViewModel as Intents/Events.
    - ViewModels only interact with the Domain layer via `UseCases`.

---

## 🎨 UI & Presentation Guidelines

### 1. File Structure (Screen vs Content)
Every UI screen MUST be split into two separate composable files to maintain separation of state and UI:

- `[FeatureName]Screen.kt`:
    - The **Stateful** component.
    - Interacts with the `ViewModel`, collects the `StateFlow`, and handles navigation/external events.
    - Passes raw data and lambda callbacks down to the Content file.
    - **Do NOT** place UI elements (Buttons, Text, etc.) directly here.

- `[FeatureName]Content.kt`:
    - The **Stateless** (Dumb) component.
    - Responsible ONLY for UI rendering (drawing the screen).
    - Receives primitive data types, UI states, and lambda functions for user interactions.
    - Must be completely isolated from `ViewModel` or remote data sources.

### 2. Component Organization
- **Feature-Specific Components:** Put custom, single-use UI elements (buttons, cards, dialogs) inside the feature's presentation directory:
  `composeApp/src/commonMain/kotlin/com/bobo/ksm/presentation/[feature_name]/components/`
- **Shared/Generic Components:** If a UI element (e.g., a primary button, standard text input) is reused across multiple screens, it MUST be placed in the global UI components directory:
  `composeApp/src/commonMain/kotlin/com/bobo/ksm/ui/components/`

### 3. Theming & Dark Mode
- The app must fully support **Light & Dark Mode**.
- Always use `MaterialTheme.colorScheme` and `MaterialTheme.typography` for styling.
- Do NOT hardcode colors or use absolute values that break when switching themes.

### 4. Compose Previews (Mandatory)
- Every `*Content.kt` file and every individual UI component MUST have a `@Preview` function.
- Previews must demonstrate both **Light** and **Dark** modes to ensure theme consistency.
- Since `*Content` and components are stateless, always generate mock data, dummy states, or use `@PreviewParameter` to populate the previews with realistic content.
- Never include `ViewModel` or real Data Layer dependencies in previews.

### 5. Navigation
- Use **Jetpack Navigation Compose** (`androidx.navigation.compose`).
- Use type-safe navigation passing Kotlin Serialization data classes or objects for routes.
- Keep navigation logic inside `[FeatureName]Screen.kt` or a dedicated Navigation graph file, never inside the `*Content.kt` file.

---

## 💡 Code Generation Rules for AI
- Always prioritize Kotlin Multiplatform standard libraries (`kotlinx.coroutines`, `kotlinx.datetime`, etc.).
- When generating UI code, always provide BOTH the `*Screen` and `*Content` implementations, along with the `@Preview` for the content/component.
- Write clean, concise, and self-documenting code.
- Omit boilerplate explanations; provide the code directly.
- If modifying an existing file, only output the changed blocks unless requested otherwise.

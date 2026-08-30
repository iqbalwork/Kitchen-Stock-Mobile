# Supabase Dashboard Setup Guide

Project: `pmzlzgetcmyusyuxyljq` (see `gradle.properties` → `supabase.project.id`). Everything here is manual dashboard/console configuration — none of it can be done from the codebase, and none of it was done as part of building the Sign Up / Forgot Password / Google Sign-In screens. Go through this checklist before relying on those features end-to-end.

## 1. Auth Providers

**Dashboard path:** Authentication → Providers

- [ ] **Email** — should already be on by default. Decide and set **"Confirm email"**:
  - If **ON** (recommended default): user must click a confirmation link before they can log in. This matches what the Sign Up screen already shows ("check your email to verify your account, then log in") — [SignUpContent.kt](../shared/src/commonMain/kotlin/com/iqbalfauzi/kitchenstock/presentation/auth/SignUpContent.kt).
  - If **OFF**: `signUpWithEmail` creates an active session immediately, and the app's `key(isLoggedIn)` recomposition in [App.kt](../shared/src/commonMain/kotlin/com/iqbalfauzi/kitchenstock/App.kt) will auto-navigate to Home instead of showing the "check your email" success state — the UI still works either way, but the copy would be misleading if confirmation is off.
- [ ] **Anonymous Sign-Ins** — toggle **"Allow anonymous sign-ins"** on. `AuthRepository.signInAnonymously()` is already wired in code but this is **off by default** in Supabase; without it the call fails.
- [ ] **Google** — enable the provider and fill in:
  - **Client ID** and **Client Secret**, obtained from Google Cloud Console (steps below).
  - No redirect URL field to fill here — Supabase auto-generates its own callback (`https://pmzlzgetcmyusyuxyljq.supabase.co/auth/v1/callback`), which is what you register in Google Cloud Console, not in the app.
- [ ] **Apple** (optional — `signInWithApple()` exists in code but is untested) — needs Services ID, Team ID, Key ID, and private key from an Apple Developer account, plus "Sign in with Apple" capability added in Xcode. Skip unless Apple Sign-In is actually wanted; the button is already in the Login screen either way.

### Google Cloud Console steps (for the Google provider above)
1. Create/select a project at [console.cloud.google.com](https://console.cloud.google.com).
2. **APIs & Services → OAuth consent screen** — configure it (app name, support email, scopes: `email`, `profile`, `openid`). Publish or keep in testing with your account added as a test user.
3. **APIs & Services → Credentials → Create Credentials → OAuth client ID**, type **Web application** (yes, Web — not Android/iOS, because the OAuth exchange happens on Supabase's server, not on-device).
4. **Authorized redirect URIs**: add `https://pmzlzgetcmyusyuxyljq.supabase.co/auth/v1/callback`.
5. Copy the generated **Client ID** and **Client Secret** into the Supabase Google provider fields from step 1 above.

## 2. URL Configuration

**Dashboard path:** Authentication → URL Configuration

- [ ] **Redirect URLs allow-list** — add `kitchenstock://login-callback`. This is the deep link scheme+host already configured in [DataModule.kt](../shared/src/commonMain/kotlin/com/iqbalfauzi/kitchenstock/di/DataModule.kt) (`scheme = "kitchenstock"`, `host = "login-callback"`) and registered in `AndroidManifest.xml`. **Without this in the allow-list, Supabase rejects the redirect even if the Google provider is enabled correctly** — this is a separate, easy-to-miss step from enabling the provider itself.
- [ ] Same URL will be needed again once iOS deep-link handling is added (see `.agents/plan/ios-oauth-deeplink.md` in the workspace root) — no dashboard change needed then, this entry already covers both platforms since they share the same scheme/host.
- [ ] **Site URL** — set to whatever makes sense as a fallback (e.g. a placeholder or future landing page URL); not used by the mobile app's deep-link flow but Supabase requires it to be set.

## 3. Database Schema & RLS

The app expects these Postgres tables (snake_case, matching the `postgrest["..."]` calls in the repositories) with columns matching the DTOs sent/received by the client:

| Table | Client DTO | Notes |
|---|---|---|
| `categories` | `CategoryDto` (`id`, `user_id?`, `name`, `icon?`) | |
| `storage_locations` | `StorageLocationDto` (`id`, `user_id?`, `name`, `description?`) | |
| `products` | `ProductDto` (`id`, `user_id?`, `category_id?`, `name`, `barcode?`, `unit`, `min_stock_level?`, `image_url?`) | |
| `inventory` | `InventoryDto` (`id`, `product_id`, `storage_location_id`, `quantity`, `expiry_date?`, `updated_at`) | **No `user_id` field at all** — see callout below |
| `shopping_list` | `ShoppingListDto` (`id`, `user_id`, `product_id`, `quantity`, `is_bought`, `created_at?`, `updated_at?`) | `user_id` required (non-null) |

- [ ] **Verify RLS is enabled on every table above**, policy pattern: `using (auth.uid() = user_id) with check (auth.uid() = user_id)`.
- [ ] **⚠️ Decide `inventory`'s ownership model before relying on RLS there.** [`InventoryRepositoryImpl.kt`](../shared/src/commonMain/kotlin/com/iqbalfauzi/kitchenstock/data/repository/InventoryRepositoryImpl.kt) never sends a `user_id` when upserting/reading `inventory` rows — the client-side code has no per-user scoping for this table. Check the actual table in the dashboard:
  - If `inventory` has no `user_id` column and RLS isn't scoping it some other way (e.g. via a join/policy on `products.user_id`), **every logged-in user can read/write every other user's inventory rows** — this needs a decision (add `user_id` + policy, or a policy joining through `products`) before this app is used by more than one real account.
  - `categories`, `storage_locations`, and `products` all accept a nullable `user_id` in their DTOs — confirm whether these are meant to be per-user or shared/global reference data (e.g. a shared category list). If shared, RLS should allow read-all but restrict writes; if per-user, enforce the same `auth.uid() = user_id` pattern as `shopping_list`.
- [ ] IDs are client-generated UUID strings (e.g. `Uuid.random().toString()` in `ShoppingRepositoryImpl.addShoppingItem`) — Postgres columns should be `uuid` or `text`, not an auto-incrementing serial.

## 4. Email Templates & Delivery

**Dashboard path:** Authentication → Email Templates / Authentication → SMTP Settings

- [ ] Supabase's built-in email sending (no custom SMTP configured) is rate-limited (a small number of emails per hour on the free tier) — fine for the manual testing done in this session, **not fine for real users**. Configure a custom SMTP provider (e.g. Resend, Postmark, SendGrid) before shipping.
- [ ] Check the **"Reset Password"** template — this is what fires from [`ForgotPasswordViewModel.sendResetLink()`](../shared/src/commonMain/kotlin/com/iqbalfauzi/kitchenstock/presentation/auth/ForgotPasswordViewModel.kt) → `auth.resetPasswordForEmail(email)`. The link in that email redirects to `kitchenstock://login-callback` (once added to the allow-list per §2) — **but there is currently no screen in the app that handles "arrived via reset-password deep link, let me set a new password."** The reset flow as built only sends the email; completing the reset (entering a new password after tapping the link) is not implemented yet. Worth a follow-up task before relying on this for real users.
- [ ] Check the **"Confirm signup"** template, relevant if "Confirm email" (§1) is on.

## 5. Suggested order of operations

1. §1 Email settings (confirm email on/off) + Anonymous sign-ins toggle — quick, unblocks testing sign up/login as-is.
2. §2 Redirect URL allow-list — required for Google Sign-In to work at all, independent of provider setup.
3. §1 Google Cloud Console + Google provider — required for the Google button to actually complete a sign-in (currently fails with `"Unsupported provider: provider is not enabled"`, verified in this session).
4. §3 RLS audit — do this before multiple real users touch the app; not urgent for solo dev testing but easy to forget once it works "well enough."
5. §4 Custom SMTP — before any real user (not just you) needs a working confirmation/reset email.

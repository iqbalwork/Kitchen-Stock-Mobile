---
name: Culinary Clarity
colors:
  surface: '#f8f9fa'
  surface-dim: '#d9dadb'
  surface-bright: '#f8f9fa'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f4f5'
  surface-container: '#edeeef'
  surface-container-high: '#e7e8e9'
  surface-container-highest: '#e1e3e4'
  on-surface: '#191c1d'
  on-surface-variant: '#3c4a42'
  inverse-surface: '#2e3132'
  inverse-on-surface: '#f0f1f2'
  outline: '#6c7a71'
  outline-variant: '#bbcabf'
  surface-tint: '#006c49'
  primary: '#006c49'
  on-primary: '#ffffff'
  primary-container: '#10b981'
  on-primary-container: '#00422b'
  inverse-primary: '#4edea3'
  secondary: '#855300'
  on-secondary: '#ffffff'
  secondary-container: '#fea619'
  on-secondary-container: '#684000'
  tertiary: '#b91a24'
  on-tertiary: '#ffffff'
  tertiary-container: '#ff7a73'
  on-tertiary-container: '#79000e'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#6ffbbe'
  primary-fixed-dim: '#4edea3'
  on-primary-fixed: '#002113'
  on-primary-fixed-variant: '#005236'
  secondary-fixed: '#ffddb8'
  secondary-fixed-dim: '#ffb95f'
  on-secondary-fixed: '#2a1700'
  on-secondary-fixed-variant: '#653e00'
  tertiary-fixed: '#ffdad7'
  tertiary-fixed-dim: '#ffb3ad'
  on-tertiary-fixed: '#410004'
  on-tertiary-fixed-variant: '#930013'
  background: '#f8f9fa'
  on-background: '#191c1d'
  surface-variant: '#e1e3e4'
typography:
  headline-lg:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 34px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: -0.01em
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 30px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
  margin-mobile: 16px
  gutter-mobile: 12px
---

## Brand & Style
The design system is built on a foundation of **Minimalism** and **Calm Utility**. The primary goal is to reduce the cognitive load of household management by providing a high-clarity, airy interface that feels as organized as a professional kitchen. 

The target audience is home cooks and organized households who value efficiency and food waste reduction. The UI evokes a sense of freshness and reliability through generous whitespace, a light-filled atmosphere, and a "clean-plate" aesthetic. It avoids visual noise, using functional color only when necessary to guide the user's attention to inventory levels and expiration dates.

## Colors
The palette is rooted in a crisp, neutral base to allow the colors of food photography and status indicators to stand out.

- **Primary (Mint Green):** Used for primary actions, active states, and "In Stock" indicators. It represents freshness and growth.
- **Secondary (Warm Orange):** Reserved strictly for "Expiring Soon" warnings. It provides a soft nudge without causing panic.
- **Tertiary (Soft Coral):** Dedicated to "Out of Stock" or "Expired" items. High visibility for immediate shopping list needs.
- **Neutral (Off-White):** The background (#F9FAFB) provides a soft, matte finish that is easier on the eyes than pure white.
- **Typography Colors:** Deep charcoal (#111827) for headings to ensure maximum contrast, and a muted slate (#4B5563) for secondary metadata.

## Typography
This design system utilizes **Inter** for its exceptional readability and neutral, systematic character. The type hierarchy is intentionally flat to maintain a "utility-first" feel. 

- **Headlines:** Use tighter letter spacing and heavier weights to create clear section anchoring.
- **Body Text:** Standard weight with generous line height (1.5x) to ensure inventory lists are easy to scan while moving around a kitchen.
- **Labels:** Used for category tags (e.g., "Dairy", "Pantry") and status badges, always in semi-bold for quick identification.

## Layout & Spacing
The layout follows a **Fluid Grid** model optimized for mobile-first interaction. 

- **Rhythm:** A 4px baseline grid governs all spacing. The standard padding for containers and screen edges is 16px (md).
- **Mobile Layout:** A 2-column or 1-column layout is used for inventory items. Cards should have a 12px gutter to maintain a light, airy feel.
- **Touch Targets:** All interactive elements (buttons, quantity toggles) maintain a minimum hit area of 44x44px.
- **Grouping:** Use 24px (lg) spacing to separate major logical sections, such as "Fresh Produce" from "Canned Goods."

## Elevation & Depth
Depth is handled through **Tonal Layers** and extremely soft shadows to maintain the minimal aesthetic.

- **Surface 0 (Background):** #F9FAFB.
- **Surface 1 (Cards/Containers):** Pure White (#FFFFFF). This creates a subtle lift against the neutral background.
- **Shadows:** Use a single, highly diffused "Ambient Shadow." Avoid multiple shadow tiers.
  - *Style:* `box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);`
- **Interactive State:** On tap, an element should not lift higher, but rather dim slightly in opacity or show a subtle inner stroke to signal engagement without breaking the flat visual plane.

## Shapes
The shape language is friendly and modern. The standard corner radius is **16px (rounded-lg)** for primary cards and input groups. 

- **Small Elements:** Checkboxes and small tags use an 8px radius.
- **Buttons:** Large buttons use a 16px radius to match the inventory cards, creating a cohesive visual language.
- **Icons:** Use icons with a consistent 2px stroke weight and rounded terminals to mirror the UI's softness.

## Components

- **Buttons:** 
  - *Primary:* Solid Mint Green (#10B981) with white text. 
  - *Secondary:* Ghost style with a 1px border of the primary color or a light gray.
- **Inventory Cards:** White background, 16px corner radius, and a subtle 1px light gray border (#F3F4F6) instead of heavy shadows. They should include a "progress bar" style indicator for quantity levels.
- **Status Chips:** 
  - Small badges with low-opacity backgrounds of the accent colors (e.g., light coral background with dark coral text for "Out of Stock").
- **Quantity Toggles:** Large, easy-to-tap "+" and "-" icons flanking the numerical value, using a subtle gray background circular shape.
- **Input Fields:** 16px rounded corners, 1px border (#E5E7EB), and clear floating labels in Inter Body-md.
- **Lists:** Use dividers only when strictly necessary; prefer whitespace (16px) to separate list items.
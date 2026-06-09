# Spec: ui-landing

**Status**: Active
**Source change**: landing-a11y-integration (active)
**Depends on**: ui-theme, ui-auth-flow

## Requirements

### Functional

- **REQ-LANDING-01**: The system MUST render a public landing page at the root path (`/`).
- **REQ-LANDING-02**: The landing page MUST detect active sessions via the server loader (`+page.server.ts`). If a session is active, the system MUST redirect the user to `/dashboard` immediately.
- **REQ-LANDING-03**: The landing page navbar MUST integrate the accessibility toolbar component (`A11yToolbar.svelte`) to enable user-triggered accessibility modes.
- **REQ-LANDING-04**: The accessibility toolbar MUST toggle the following modes:
  - **High Contrast**: toggles the `.a11y-high-contrast` class on `document.documentElement` to override layout colors with maximum contrast.
  - **Grayscale**: toggles the `.a11y-grayscale` class on `document.documentElement` to apply a grayscale filter.
  - **Large Font**: toggles the `html.a11y-large-font` class on `document.documentElement` to scale root font size to `120%`.
  - **Underline Links**: toggles the `.a11y-underline-links` class on `document.documentElement` to force underlines on all `a` elements.
- **REQ-LANDING-05**: The landing page footer MUST use a fixed, high-contrast dark palette (background `#020329`, text `#ffffff` and `#e2e8f0`) to guarantee legibility regardless of the active global dark/light theme.

### Scenarios (GWT)

#### Scenario: Public Access to Landing Page
- **Given** an unauthenticated visitor
- **When** they navigate to `/`
- **Then** they see the landing page with Hero, Features, Copilot showcase, pricing, and footer.

#### Scenario: Auto-Redirect for Logged-In Users
- **Given** an authenticated user (session cookie present)
- **When** they attempt to load `/`
- **Then** the server loader redirects them to `/dashboard` immediately with code `303`.

#### Scenario: Toggling High Contrast Mode
- **Given** a user on the landing page
- **When** they click the "Alto contraste" (HC) button in the toolbar
- **Then** the `.a11y-high-contrast` class is appended to `<html>`
- **And** all core CSS variables (bg, text, borders) swap to high-contrast colors (e.g. black, white, and primary blue).

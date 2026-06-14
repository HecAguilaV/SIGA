# SIGA Design System

*Leer en otros idiomas: [![Español](https://img.shields.io/badge/Language-Espa%C3%B1ol-green)](../../es/arquitectura/SISTEMA_DISENO.md)*

## 1. Visual Theme & Atmosphere
- **Concept:** "Linear-Inspired Technical Precision"
- **Vibe:** Ultra-minimalist, precise, high-density dashboard, developer-centric.
- **Lighting:** Dark mode by default. Deep space voids with sharp, high-contrast text and subtle glowing accents.
- **Shape Language:** Sharp corners with very slight rounding (`2px` to `6px`). Zero-fluff. Hairline borders.

## 2. Color Palette & Roles

### Base & Surfaces
| Name | Hex | Usage |
|------|-----|-------|
| `canvas` | `#0E0F11` | Main application background. True void. |
| `surface-primary` | `#1A1B1E` | Primary cards, panels, modals. Barely elevated. |
| `surface-secondary`| `#25262B` | Hover states, secondary panels, inputs. |
| `surface-elevated` | `#2C2E33` | Dropdowns, popovers, active elements. |

### Typography
| Name | Hex | Usage |
|------|-----|-------|
| `text-primary` | `#FFFFFF` | Headings, primary values. High contrast. |
| `text-secondary`| `#A1A1AA` | Body copy, labels, secondary information. |
| `text-tertiary` | `#71717A` | Placeholder text, disabled states, timestamps. |

### Accents & Semantic
| Name | Hex | Usage |
|------|-----|-------|
| `accent-primary`| `#5E6AD2` | Linear purple. Primary CTAs, active states, glowing indicators. |
| `accent-hover`  | `#737EE0` | Focus rings, hover states for primary accents. |
| `status-success`| `#10B981` | Completed, online, positive metrics (Emerald). |
| `status-warning`| `#F59E0B` | Pending, alerts (Amber). |
| `status-danger` | `#EF4444` | Errors, destructive actions (Red). |
| `border-subtle` | `rgba(255,255,255,0.06)` | Dividers, panel outlines. |

## 3. Typography Rules
- **Font Family:** `Inter`, `SF Pro Display`, `system-ui`, sans-serif.
- **Tracking (Letter Spacing):** Tighter for headings (`-0.02em`), neutral for body.
- **Scale:**
  - `Display:` 36px, Weight 600, Tracking -0.03em, `text-primary`
  - `H1:` 24px, Weight 600, Tracking -0.02em, `text-primary`
  - `H2:` 18px, Weight 500, Tracking -0.01em, `text-primary`
  - `Body:` 14px, Weight 400, Line-height 1.6, `text-secondary`
  - `Caption:` 12px, Weight 500, `text-tertiary`
  - `Mono:` `JetBrains Mono`, `ui-monospace`. 13px. Used for SKUs, IDs, Code.

## 4. Component Stylings

### Action Buttons
- **Primary:** Background `accent-primary`, Text `#FFF`, Border none, Radius `6px`, Height `32px`, Padding `0 12px`, Font size `13px`, Weight `500`. Shadow: `0 2px 4px rgba(0,0,0,0.2), inset 0 1px 0 rgba(255,255,255,0.1)`.
- **Secondary:** Background `transparent`, Text `text-secondary`, Border `1px solid border-subtle`, Radius `6px`. Hover: Background `surface-secondary`, Text `text-primary`.

### Data Cards (KPIs / Insights)
- Background: `surface-primary`.
- Border: `1px solid border-subtle`.
- Border Radius: `8px`.
- Padding: `16px`.
- Transition: `border-color 0.2s ease, transform 0.2s ease`.
- Hover State: Border becomes `rgba(255,255,255,0.15)`, transform `translateY(-1px)`.

### Tables
- Header: Font size `12px`, Weight `500`, Color `text-tertiary`, Text-transform `uppercase`, Letter-spacing `0.05em`. Border bottom `1px solid border-subtle`.
- Row: Font size `13px`, Color `text-primary`. Border bottom `1px solid border-subtle`. Height `40px`.
- Hover: Row background `surface-secondary`.

### Navigation (Sidebar / Topbar)
- Background: `canvas`.
- Border: Right border `1px solid border-subtle` (Sidebar) or Bottom border (Topbar).
- Items: `text-secondary`. Hover: `text-primary`, background `surface-secondary`, radius `6px`.
- Active Item: Text `text-primary`, background `rgba(94, 106, 210, 0.1)`, left border `2px solid accent-primary`.

## 5. Depth & Elevation
- **Level 0 (`canvas`):** Page background. No shadows.
- **Level 1 (`surface-primary`):** Cards. Shadow: `0 1px 2px rgba(0,0,0,0.3)`.
- **Level 2 (`surface-elevated`):** Dropdowns. Shadow: `0 8px 24px rgba(0,0,0,0.4), 0 0 0 1px border-subtle`. Backdrop-filter: `blur(12px)`.

## 6. Evolution: Stitch v2.4 Agentic (June 2026)
As of June 2026, SIGA adopts the **Stitch** visual language, optimized for AI-assisted interfaces (Agentic UI) and predictive data visualization.

### Stitch Visual Identity
- **Atmosphere:** "Glassmorphism & High-Tech Teal".
- **Primary Color:** `#009579` (SIGA Emerald/Teal).
- **Typography:** `Hanken Grotesk` (Google Fonts).
- **Key Components:**
  - **Glass Cards:** Backgrounds with `backdrop-filter: blur(8px)` and semi-transparent borders.
  - **AI Shimmer:** Subtle gradient animations (`#009579/10`) to indicate AI processing.
  - **Bento Grids:** Modular layouts for high-density dashboards.

### Dashboard Application
- **POS:** Simplified interface with real-time SAGA visual feedback (SSE).
- **Analytics:** Integrated charts with AI-powered narratives.

---

## 7. Do's and Don'ts
- **DO** use absolute black or very dark gray for backgrounds to let colors pop.
- **DO** use typography size to establish hierarchy instead of colors.
- **DO** apply `Hanken Grotesk` for customer-facing interfaces (Dashboard/POS).
- **DON'T** use borders thicker than `1px`.
- **DON'T** use border radii greater than `12px` in the Stitch system.
- **DON'T** use large colorful banners. Keep accents for data and critical actions.


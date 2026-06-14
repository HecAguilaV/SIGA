# Proposal: POS UI with Stitch Design (Isolated)

## Intent
Implement a fully functional Point of Sale (POS) interface in `apps/dashboard` that integrates with the `siga-sales` microservice. The UI will adopt the "Stitch" design language (Tailwind, Material Symbols, Glassmorphism) while keeping it isolated to the `/pos` route to maintain architectural and visual independence from the legacy UI kit.

## Scope

### In Scope
- **Stitch Design Language Implementation:** Scoped CSS variables and Tailwind classes for the POS view.
- **Cash Shifts (Turnos):** Integration with `/api/v1/sales/cash-shifts` to manage store sessions.
- **Product Search:** Optimized search for POS using the existing inventory API.
- **Cart Management:** Reactive state for sales processing.
- **Checkout Flow:** Integration with the Sales SAGA (via BFF) and async success/failure feedback.
- **Responsive Layout:** Optimized for both Desktop and Tabletviewports.

### Out of Scope
- **Fiscal Printing:** (Deferred to Billing module).
- **Offline Mode:** (Planned for future PWA phase).
- **Global Theme Migration:** The rest of the dashboard remains in the current design standard.

## Capabilities

### New Capabilities
- `ui-pos-stitch`: New interface for the Point of Sale with the Stitch visual standard.
- `ui-sales-checkout`: Integration flow between frontend cart and backend sales SAGA.
- `ui-cash-shifts`: UI components for opening/closing cash shifts.

### Modified Capabilities
- `ui-bff`: Update the BFF layer to proxy Sales and Cash Shifts endpoints.

## Approach
- **Isolated Styling:** Define a `.pos-theme` class that overrides CSS variables (colors, fonts) only for the POS route.
- **Server-Side Composition (BFF):** Use `+page.server.ts` to fetch products and verify shift status before rendering.
- **Material Symbols:** Load the font dynamically or specifically for this route.
- **Reactive State:** Use Svelte 5 `$state` and `$derived` for the cart and totals.

## Phases
1. **Design & Spec:** Formalize the contracts and UI components.
2. **Infrastructure:** Setup the isolated theme and BFF proxies.
3. **Core POS:** Search, Cart, and Layout implementation.
4. **Integration:** Cash Shifts and Checkout flow implementation.
5. **Verification:** End-to-end testing with mock/real backend.

## Risks
- **Design Inconsistency:** The contrast between POS and the rest of the app might be jarring (accepted per user request).
- **Backend Sync:** SAGA pattern is async; the UI must handle the "Wait" state correctly to avoid double checkout.

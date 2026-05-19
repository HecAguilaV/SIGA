# User Stories — SIGA

*Leer en otros idiomas: [![Español](https://img.shields.io/badge/Language-Espa%C3%B1ol-green)](../es/HISTORIAS_USUARIO.md)*

> **Purpose**: Capture the end-user perspective as a complement to the Core Manifesto and SDD specifications.
> Each story includes acceptance criteria in **Given/When/Then** format ready to translate into Kotest BehaviorSpec.
>
> **Origin**: These stories are born from direct work experience in a Chilean food service SME — not from generic templates. The actors are real people.

---

## Personas

| Persona | Role | Real Context | Pain Point |
|---------|------|--------------|------------|
| **Elizabeth** | SME Owner | Manages multiple contracted cafeterias, catering events, kiosks, and a central kitchen. Works 6am to midnight. Not digitally native. Manages everything with a notebook, notes, and WhatsApp. No Excel. On Sundays she tries to build weekly merchandise distribution lists. | Spends hours comparing supplier prices with notebook in hand, asking via WhatsApp what's missing at each location. Zero real-time stock visibility. |
| **Héctor** | Driver/delivery, kiosk admin, inventory manager | 95% of time delivering merchandise. No time for kiosk administration or stock entry. Found massive discrepancies: products showing stock in the system didn't exist in reality; products showing as depleted had overstock and expired items. The ERP was slow with bad UX: filters reset on save, case-sensitive search. No product code policy led to duplicate products with different codes and prices. | The system doesn't reflect reality. It perpetuates drift instead of correcting it. |
| **Yesenia** | Kiosk operator/cashier | Registers sales, requests product additions when the owner buys items without informing anyone. Must manually track remaining stock. Found discrepancies from duplicate products with different codes. Case-sensitive search confused her. | Can't sell what's not in the system, and can't find what is. |
| **Luis** | Driver/delivery and purchasing | Works Monday to Friday. Everything coordinated by voice or WhatsApp with Elizabeth. Some kitchens contact him directly when they need something. He requests from the warehouse manager. | No formal record of what he delivers or what's requested. Everything is verbal. |
| **Antonia** | Warehouse manager | Manages the central warehouse, delivers products to cafeterias, central kitchen, and some kiosk supplies. No paper or digital management — all word-of-mouth. Products existed in the ERP but nobody managed them. | Zero traceability. If asked what she delivered last week, she doesn't know. |
| **Héctor** *(as SIGA Admin)* | Platform administrator | Builds, operates, and monitors SIGA. Spends his remaining 5% keeping the platform running: manages tenants, checks microservice health, and ensures Elizabeth, Yesenia, Luis, and Antonia can work without friction. | Needs system health visibility to catch problems before users report them. |

---

## Epic 1: Identity and Access

> *Pillar: Sovereign Microservices Architecture — `siga-auth`*

### US-1.1 — SaaS Owner Onboarding

**As** Elizabeth (SME owner),
**I want** to register on SIGA with my email and business details,
**so that** I can start managing my cafeterias and kiosks without depending on anyone.

```gherkin
Scenario: Successful registration with email verification
  Given Elizabeth enters her email, company name, and password on the customer-portal
  When she submits the registration form
  Then a Customer is created with PENDING_VERIFICATION status
  And a verification email is sent with a 24-hour token

Scenario: Verification and first access
  Given Elizabeth clicks the verification link within 24 hours
  Then her account transitions to ACTIVE, her User with OWNER role is generated, and her tenant is created
```

**SDD Ref**: `openspec/specs/customer-auth/spec.md`

---

### US-1.2 — Granular Permissions for Multi-role Employees

**As** Elizabeth (SME owner),
**I want** to assign specific permissions to each employee based on their actual responsibilities,
**so that** Héctor manages kiosks, Yesenia sells, and Luis only sees what he needs for deliveries.

```gherkin
Scenario: Assign multiple roles to a multi-function employee
  Given Héctor is a User with EMPLOYEE role in Elizabeth's tenant
  When Elizabeth assigns INVENTORY_READ, INVENTORY_WRITE, and KIOSK_ADMIN
  Then Héctor can view stock, add products, and manage kiosks
  And cannot delete products (requires INVENTORY_DELETE)

Scenario: Minimal permissions for delivery driver
  Given Luis is a User with EMPLOYEE role
  When Elizabeth assigns INVENTORY_READ and DELIVERY_VIEW
  Then Luis can see what products to deliver and where
  And cannot modify stock or prices

Scenario: AI agent inherits user permissions
  Given Héctor has INVENTORY_WRITE
  When he asks the agent "add 50 napkin packs to the North kiosk"
  Then the agent executes the action because Héctor has permission
  And logs that it was executed on behalf of Héctor
```

---

## Epic 2: Inventory Management — The Heart of SIGA

> *Pillar: Asset Management as the Center — `siga-inventory`*
> *This is the critical point. This is where the SME bleeds time and money.*

### US-2.1 — Real Multi-location Stock Visibility

**As** Elizabeth (owner with cafeterias, kiosks, and central kitchen),
**I want** to see stock across all my locations on a single screen,
**so that** I don't have to ask via WhatsApp what's missing at each place.

```gherkin
Scenario: Consolidated stock view
  Given Elizabeth has 3 kiosks, 2 cafeterias, and 1 central kitchen
  And "Napkins 100pk" has: North Kiosk (20), South Kiosk (5), Warehouse (200)
  When she accesses the inventory dashboard
  Then she sees "Napkins 100pk" with total stock: 225
  And can expand the breakdown by location

Scenario: Filter by operational point
  Given Elizabeth is viewing consolidated inventory
  When she filters by "North Kiosk"
  Then she only sees products and quantities for that location
```

---

### US-2.2 — Quick Frictionless Product Entry

**As** Héctor (kiosk admin, always racing the clock),
**I want** to add products to the system quickly without unnecessary steps,
**so that** I stop perpetuating the gap between system stock and reality.

```gherkin
Scenario: Add product with automatic code
  Given Héctor needs to enter a new product purchased in an emergency
  When he creates "Assorted Cookies 250g" with the SKU field left empty
  Then the system generates an automatic SKU based on category + sequence
  And the product is immediately available for sale at kiosks

Scenario: Duplicate product detection
  Given "Assorted Cookies 250g" already exists with SKU "COO-001"
  When Héctor tries to create "Assorted Cookie 250g" (name variation)
  Then the system warns: "Similar product found: Assorted Cookies 250g (COO-001). Same product?"
  And offers options: "Use existing" / "Create as new"
```

---

### US-2.3 — Search That Works for Everyone

**As** Yesenia (cashier, not digitally native),
**I want** to search for products without being punished for capitalization or accents,
**so that** I can find what I need without frustration.

```gherkin
Scenario: Case-insensitive and accent-insensitive search
  Given the product "Café Instantáneo 200g" exists
  When Yesenia types "cafe instantaneo" in the search bar
  Then the system finds "Café Instantáneo 200g"
  And results appear in under 500ms

Scenario: Partial name search
  Given "Assorted Cookies", "Salted Cookies", and "Water Crackers" exist
  When Yesenia types "cooki"
  Then she sees both cookie products in the results
```

---

### US-2.4 — Stock Reconciliation (Closing the Gap)

**As** Héctor (inventory manager),
**I want** to record the physical stock I actually see and have the system detect differences,
**so that** I can fix the drift between what the system says and what's really there.

```gherkin
Scenario: Physical count with drift detection
  Given the system says "Juice Box 1L" has 45 units at North Kiosk
  When Héctor records a physical count of 12 units
  Then the system flags a drift of -33 units
  And requests a reason: "Shrinkage" / "Theft" / "Expired" / "Entry error" / "Other"
  And adjusts stock to the real value (12)
  And logs the event in the audit trail

Scenario: Expired product detection via count
  Given Héctor marks 8 units of "Natural Yogurt" as "Expired" during count
  Then stock is reduced by 8 units
  And an alert is generated for Elizabeth: "8 units of Natural Yogurt expired at North Kiosk"
```

---

### US-2.5 — Warehouse Management with Traceability

**As** Antonia (warehouse manager),
**I want** to record what products leave the warehouse and where they go,
**so that** when someone asks what I delivered, I can answer with certainty.

```gherkin
Scenario: Record warehouse exit to operational point
  Given Antonia has 200 units of "Napkins 100pk" in the warehouse
  When she records an exit of 50 units to "School Cafeteria"
  Then warehouse stock drops to 150
  And School Cafeteria stock increases by 50
  And a record is created: date, product, quantity, origin, destination, responsible (Antonia)

Scenario: Delivery history query
  Given someone asks "What did you deliver to School Cafeteria last week?"
  When Antonia queries the history filtered by destination and date
  Then she sees the complete delivery list with date, product, quantity
```

---

## Epic 3: Sales and POS

> *Pillar: Sales Module with Purpose — `siga-sales` + SAGA via Kafka*

### US-3.1 — Sale with Automatic Stock Deduction

**As** Yesenia (kiosk cashier),
**I want** to register a sale and have stock deducted automatically,
**so that** I don't have to notify Héctor every time I sell something.

```gherkin
Scenario: Successful sale with sufficient stock
  Given Yesenia is at the POS for North Kiosk
  And "Juice Box 1L" has 12 units in stock
  When she registers a sale of 3 units
  Then siga-sales creates the transaction, publishes "sale.completed" via Kafka
  And siga-inventory deducts 3 units → stock becomes 9

Scenario: Sale rejected due to insufficient stock
  Given "Assorted Cookies" has 1 unit at North Kiosk
  When Yesenia tries to sell 3 units
  Then the system rejects: "Insufficient stock. Available: 1"
  And stock is not modified
```

**SDD Ref**: `openspec/changes/saga-sales-inventory/spec.md`

---

### US-3.2 — Unregistered Product Request

**As** Yesenia (kiosk cashier),
**I want** to request a product addition when Elizabeth buys something without informing anyone,
**so that** I can sell it without waiting for Héctor to be available.

```gherkin
Scenario: New product addition request
  Given Elizabeth bought "BBQ Flavor Chips" and left them at the kiosk without notice
  And the product doesn't exist in the system
  When Yesenia creates a request with: name, suggested price, and photo
  Then the request is pending approval by a user with INVENTORY_WRITE
  And Héctor receives a notification: "Yesenia requests adding: BBQ Flavor Chips"

Scenario: Quick request approval
  Given Héctor receives Yesenia's request
  When he approves it after reviewing name and price
  Then the system creates the product with automatic SKU
  And Yesenia can now register sales for that product
```

---

## Epic 4: The AI Agent — The Magic of SIGA

> *Pillar: Operative AI Agents — `siga-agent` + A2UI*
> *"Don't manage your inventory, manage your time."*
>
> *Elizabeth manages everything with a notebook and WhatsApp. The AI agent is her natural evolution: the same conversation, but with an intelligent system behind it.*

### US-4.1 — Conversational Stock Entry During Delivery

**As** Héctor (delivery driver with no time to open forms),
**I want** to tell the agent what I'm delivering while on the route,
**so that** stock updates without me sitting at a desk.

```gherkin
Scenario: Conversational delivery logging
  Given Héctor has the Advanced Plan and INVENTORY_WRITE permission
  When he writes to the agent: "Just dropped off 30 juice boxes and 20 assorted cookies at North Kiosk"
  Then the agent interprets: Juice Box 1L (+30, North Kiosk), Assorted Cookies (+20, North Kiosk)
  And asks for confirmation showing an A2UI summary
  When Héctor confirms
  Then stock is updated for both products at North Kiosk
```

---

### US-4.2 — Intelligent Shopping List

**As** Elizabeth (owner who builds lists on Sunday afternoons),
**I want** to ask the agent to build my weekly shopping list,
**so that** I don't have to do it by hand looking at my notebook.

```gherkin
Scenario: Stock-based shopping list generation
  Given Elizabeth has the Advanced Plan
  When she writes: "What do I need to buy this week for the kiosks?"
  Then the agent analyzes current stock vs average weekly consumption per product
  And generates an A2UI list with: product, suggested quantity, highest-consuming location
  And Elizabeth can edit quantities and confirm the list
```

---

### US-4.3 — Anomaly Detection

**As** Elizabeth (owner),
**I want** the agent to alert me when it detects something unusual in inventory,
**so that** I find out before it becomes a problem.

```gherkin
Scenario: Proactive expiration risk alert
  Given the agent periodically analyzes inventory
  And detects "Natural Yogurt" has had no movement for 15 days at South Kiosk with 40 units
  Then it sends an alert: "Natural Yogurt at South Kiosk: 40 units with no movement in 15 days. Possible expiration risk."

Scenario: Anomalous drift detection
  Given the system shows 50 units of "Soda 500ml" at North Kiosk
  And registered sales in the last week are only 5
  And the last physical count (3 days ago) recorded 20 units
  Then the agent alerts: "Drift detected in Soda 500ml — system says 50 but last count recorded 20"
```

---

### US-4.4 — Dual-mode: Classic ↔ Agentive

**As** Elizabeth (owner, not digitally native),
**I want** to talk to the agent when it feels easier than navigating menus,
**so that** I can use SIGA like an intelligent WhatsApp.

```gherkin
Scenario: Transition from classic to agentive mode
  Given Elizabeth is browsing inventory in classic mode
  When she clicks "Let's save time: SIGA"
  Then the agent expands with current page context
  And suggests: "You're in inventory. Want me to analyze what's missing at your kiosks?"

Scenario: User plan enforcement
  Given Elizabeth has the Base Plan (analysis only)
  When she writes: "Add 50 napkins to North Kiosk"
  Then the agent responds: "That feature requires the Advanced Plan. Want to see the options?"
  And does not execute the action
```

**SDD Ref**: `openspec/specs/agent-service/spec.md`, `openspec/specs/ui-a2ui/spec.md`

---

## Epic 5: SaaS Billing

> *Pillar: Business Model — `siga-billing`*

### US-5.1 — Subscriptions and Plans

**As** Elizabeth (SME owner),
**I want** to choose a plan based on my needs,
**so that** I only pay for what I need and can scale when I grow.

```gherkin
Scenario: Plan selection during onboarding
  Given Elizabeth just verified her account
  When she accesses the customer-portal for the first time
  Then she sees Base Plan ($X/month: AI analysis) and Advanced Plan ($Y/month: operative AI with CRUD)
  And can select and proceed to payment

Scenario: Plan upgrade activates operative AI
  Given Elizabeth has the Base Plan
  When she requests an upgrade to the Advanced Plan
  Then Operative AI capabilities are activated immediately
  And the agent can now execute CRUD actions on inventory
```

**SDD Ref**: `openspec/changes/billing-uuid-hexagonal/`

---

## Epic 6: Platform Administration

### US-6.1 — Tenant Visibility and Health

**As** Héctor (SIGA admin),
**I want** to see all customers' status and microservice health,
**so that** I can catch problems before users report them.

```gherkin
Scenario: Tenant dashboard
    Given Héctor accesses the admin-portal
  Then she sees a customer list with: company, plan, status, registration date
  And can filter by plan and status

Scenario: Health monitor
  Given Valentina accesses the health panel
  Then she sees each microservice status (UP/DOWN) via Eureka
  And if one is DOWN, a visual alert is displayed
```

---

## Traceability: User Story → BDD → SDD → Test

```
US-3.1 (Sale with stock deduction)
  → Scenario: "Successful sale with sufficient stock" (BDD)
  → openspec/changes/saga-sales-inventory/spec.md (SDD)
  → SaleCompletedBehaviorSpec.kt (Kotest BehaviorSpec)

US-2.1 (Multi-point consolidated stock)
  → Scenario: "Consolidated stock view"
  → openspec/changes/inventory-core-features/specs/consolidated-stock-view/spec.md (SDD)
  → ConsolidatedStockQueryTest.kt (Kotest)

US-2.2 (Frictionless product entry)
  → Scenario: "Add product with auto-generated code"
  → openspec/changes/inventory-core-features/specs/product-creation-flow/spec.md (SDD)
  → CreateProductUseCaseTest.kt (Kotest)

US-2.3 (Search that works for everyone)
  → Scenario: "Case-insensitive and unaccented search"
  → openspec/changes/inventory-core-features/specs/inventory-search/spec.md (SDD)
  → InventorySearchTest.kt (Kotest)

US-2.4 (Stock reconciliation)
  → Scenario: "Physical count with discrepancy detection"
  → openspec/changes/inventory-core-features/specs/stock-reconciliation/spec.md (SDD)
  → ReconcileStockUseCaseTest.kt, ReconciliationAlertTest.kt (Kotest)

US-2.5 (Warehouse management with traceability)
  → Scenario: "Warehouse exit to operational point"
  → openspec/changes/inventory-core-features/specs/warehouse-transfer/spec.md (SDD)
  → TransferStockUseCaseTest.kt, TransferAtomicityTest.kt (Kotest)
```

> Each story is traceable to an automated test. The Harness pipeline executes TDD → BDD → SDD in sequence.

---

## ID Convention

| Prefix | Epic |
|--------|------|
| US-1.x | Identity and Access |
| US-2.x | Inventory Management |
| US-3.x | Sales and POS |
| US-4.x | AI Agent (A2UI) |
| US-5.x | SaaS Billing |
| US-6.x | Platform Administration |

---

*Stories derived from the Core Manifesto, SIGA Vision, and direct work experience in Chilean food service SMEs.*
*The actors are real people. The pain points were lived, not assumed.*

---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`

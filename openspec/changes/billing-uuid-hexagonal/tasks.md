# Tasks: billing-uuid-hexagonal

## Phase 1: Infrastructure & Ports
- [ ] 1.1 Create `com.siga.billing.domain.port.PaymentGateway` interface.
- [ ] 1.2 Create `com.siga.billing.domain.model.PaymentRequest` and `PaymentResponse` DTOs.
- [ ] 1.3 Create `com.siga.billing.infrastructure.adapter.TransbankFictitiousAdapter`.

## Phase 2: Entity Migration (TDD)
- [ ] 2.1 [RED] Create `CustomerPersistenceTest` with UUID expectations.
- [ ] 2.2 [GREEN] Refactor `Customer.kt` to UUID and fix repository.
- [ ] 2.3 [RED] Create `SubscriptionPersistenceTest` with UUID expectations.
- [ ] 2.4 [GREEN] Refactor `Subscription.kt` to UUID and fix repository.
- [ ] 2.5 [RED] Create `PaymentPersistenceTest` with UUID expectations.
- [ ] 2.6 [GREEN] Refactor `Payment.kt` to UUID and fix repository.

## Phase 3: Domain Service
- [ ] 3.1 Create `com.siga.billing.service.SubscriptionService` to use `PaymentGateway`.
- [ ] 3.2 Implement `processSubscriptionPayment` in `SubscriptionService`.

## Phase 4: Controller Modernization
- [ ] 4.1 Refactor `CustomerController.kt` to use UUID and `/api/v1/billing/customers`.
- [ ] 4.2 Refactor `PaymentController.kt` to use UUID and `/api/v1/billing/payments`.
- [ ] 4.3 Refactor `SubscriptionController.kt` to use UUID and `/api/v1/billing/subscriptions`.

## Phase 5: Verification
- [ ] 5.1 Run all tests in `billing` microservice.
- [ ] 5.2 Verify logs for fictitious Transbank processing.

# Billing Coverage Junio 2026 — Tasks

## ✅ Completed

### Domain Model Tests
- [x] Payment + PaymentStatus enum tests
- [x] Subscription + BillingPeriod + SubscriptionStatus enum tests
- [x] Plan model tests
- [x] Customer model tests (all fields, null optionals, trial dates)
- [x] PaymentRequest tests (default CLP currency)
- [x] PaymentResponse tests (success/failure scenarios)
- [x] SaleInvoice + SaleInvoiceStatus enum tests

### Entity Tests  
- [x] CustomerEntity tests
- [x] PaymentEntity tests
- [x] PlanEntity tests
- [x] Invoice tests
- [x] ShoppingCart tests
- [x] SubscriptionEntity tests
- [x] SaleInvoiceEntity tests

### Mapper Tests
- [x] CustomerMapper (toDomain, toEntity, roundtrip)
- [x] PlanMapper (toDomain, toEntity, roundtrip)
- [x] PaymentMapper (toDomain, toEntity, roundtrip, status mappings)
- [x] SubscriptionMapper (toDomain, toEntity, roundtrip, period+status mappings)
- [x] SaleInvoiceMapper (toDomain, toEntity, roundtrip, status mappings)

### Controller Tests (with Security Audit)
- [x] PlanController: CRUD + 501s + input validation
- [x] PaymentController: CRUD + customer filter + UUID validation
- [x] SubscriptionController: CRUD + payment flow + amount validation
- [x] CustomerController: CRUD + email lookup + input validation

### Event Tests
- [x] BillingInvoiceConsumer: valid events, minimum fields
- [x] SaleCompletedEvent: creation with defaults

### Config Tests
- [x] KafkaConfig: bean creation verification

### Use Case Tests (enhanced)
- [x] Payment failure path
- [x] Empty subscriptions list
- [x] Not found scenarios
- [x] Active subscriptions filtering

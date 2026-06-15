# post-verification-onboarding Specification

## Purpose

Define the post-verification onboarding flow that collects name and companyName from users who registered with only email+password.

## Requirements

### R1: Onboarding Page

After email verification, the frontend MUST redirect to an onboarding page when the Customer has a placeholder name (matches email prefix).

#### Scenario: Redirect to onboarding after verification

- GIVEN a Customer who registered with only email+password
- WHEN they click the verification link
- THEN after successful verification they are redirected to the onboarding page
- AND the onboarding page shows name and companyName fields

#### Scenario: Skip onboarding for full profiles

- GIVEN a Customer who registered with all four fields
- WHEN they verify their email
- THEN they are redirected to the dashboard directly — no onboarding

### R2: Profile Update

The onboarding page MUST submit name and companyName via `PUT /api/v1/auth/customers/{id}`.

#### Scenario: Successful onboarding

- GIVEN a verified Customer with placeholder name
- WHEN they submit "Maria García" as name and "Mi Empresa SRL" as companyName
- THEN PUT /api/v1/auth/customers/{id} returns 200
- AND Customer.name is updated to "Maria García"
- AND Customer.companyName is updated to "Mi Empresa SRL"
- AND the frontend redirects to the dashboard

#### Scenario: Skip onboarding

- GIVEN a verified Customer on the onboarding page
- WHEN they click "Skip" or "Do this later"
- THEN they are redirected to the dashboard
- AND the Customer can return to onboarding later via profile settings

## Security Requirements

| Control | Mitigation |
|---------|------------|
| Customer owns their data | PUT endpoint MUST verify JWT matches the target Customer ID — no cross-tenant update |
| Placeholder detection | Frontend SHOULD detect placeholder name by comparing to email prefix, backend MAY expose a `hasPlaceholderName` flag |

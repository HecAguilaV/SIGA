# Delta for customer-auth

## ADDED Requirements

### R11: Password Reset Request

`POST /api/v1/auth/reset-password/request` with `{ email }`. The system MUST generate a scoped reset token (15min expiry), persist it, publish a PASSWORD_RESET EmailEvent to `email-events`, and return 200 regardless of whether the email exists (no user enumeration).

#### Scenario: Existing email requests reset

- GIVEN a registered customer with email "a@b.com"
- WHEN POST /api/v1/auth/reset-password/request with `{ email: "a@b.com" }`
- THEN 200 + generic message
- AND reset token created with 15-minute expiry
- AND PASSWORD_RESET EmailEvent published to `email-events`

#### Scenario: Non-existing email returns 200

- GIVEN no customer with email "unknown@b.com"
- WHEN POST /api/v1/auth/reset-password/request with `{ email: "unknown@b.com" }`
- THEN 200 — no user enumeration
- AND no event published, no token created

#### Scenario: Missing email field

- GIVEN a request without an email field
- WHEN POST /api/v1/auth/reset-password/request with `{}`
- THEN 400 Bad Request

### R12: Password Reset Confirm

`POST /api/v1/auth/reset-password/confirm` with `{ token, newPassword }`. The system MUST validate the token, verify expiry, hash the new password, update it, invalidate the token (one-time use), and return 204 No Content.

#### Scenario: Successful reset

- GIVEN a valid, non-expired reset token for customer "a@b.com"
- WHEN POST /api/v1/auth/reset-password/confirm with `{ token, newPassword }`
- THEN 204 No Content
- AND password updated (BCrypt)
- AND token invalidated

#### Scenario: Expired token

- GIVEN a reset token older than 15 minutes
- WHEN POST /api/v1/auth/reset-password/confirm with the expired token
- THEN 410 Gone

#### Scenario: Invalid token

- GIVEN a non-existent reset token
- WHEN POST /api/v1/auth/reset-password/confirm
- THEN 404 Not Found

## MODIFIED Requirements

### R1: Customer Registration

`POST /api/v1/auth/register`. Creates Customer (`isActive=false`), publishes a WELCOME EmailEvent to `email-events`, returns `201 Pending`. `EmailSenderPort` is retained for backward compatibility but is no longer called by the use case.
(Previously: called `EmailSenderPort.sendVerificationEmail` synchronously)

#### Scenario: Successful registration

- GIVEN a valid request (email, password, name, companyName)
- WHEN POST /api/v1/auth/register
- THEN 201 + `{ status: "pending" }`
- AND Customer created with BCrypt password hash, `isActive=false`

#### Scenario: Event published instead of SMTP

- GIVEN a valid registration request
- WHEN `RegisterCustomerUseCase.register` completes
- THEN a WELCOME EmailEvent is published to `email-events`
- AND `EmailSenderPort.sendVerificationEmail` is NOT called

#### Scenario: Duplicate email (unchanged)

- GIVEN an existing Customer with email "a@b.com"
- WHEN POST /api/v1/auth/register with email "a@b.com"
- THEN 409 Conflict

#### Scenario: Missing required fields (unchanged)

- GIVEN a request without email/password/name/companyName
- WHEN POST /api/v1/auth/register
- THEN 400 Bad Request

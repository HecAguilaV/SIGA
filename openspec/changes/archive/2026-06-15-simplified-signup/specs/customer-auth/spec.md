# Delta for customer-auth

## MODIFIED Requirements

### R1: Customer Registration (modified — async email)

`POST /api/v1/auth/register`. Creates Customer (`isActive=false`), publishes a WELCOME EmailEvent to `email-events`, returns `201 Pending`. `EmailSenderPort` is retained for backward compatibility but is no longer called by the use case.
(Previously: name/companyName mandatory; now optional with email-as-name fallback)

#### Scenario: Successful registration with full details

- GIVEN a valid request (email, password, name, companyName)
- WHEN POST /api/v1/auth/register
- THEN 201 + `{ status: "pending" }`
- AND Customer created with BCrypt password hash, `isActive=false`

#### Scenario: Successful minimal registration

- GIVEN a request with only email and password (no name, no companyName)
- WHEN POST /api/v1/auth/register
- THEN 201 + `{ status: "pending" }`
- AND Customer created with `name` set to email prefix (substring before `@`)

#### Scenario: Event published instead of SMTP

- GIVEN a valid registration request (full or minimal)
- WHEN `RegisterCustomerUseCase.register` completes
- THEN a WELCOME EmailEvent is published to `email-events`
- AND `EmailSenderPort.sendVerificationEmail` is NOT called

#### Scenario: Duplicate email

- GIVEN an existing Customer with email "a@b.com"
- WHEN POST /api/v1/auth/register with email "a@b.com"
- THEN 409 Conflict

#### Scenario: Missing required fields

- GIVEN a request without email or password
- WHEN POST /api/v1/auth/register
- THEN 400 Bad Request

## Security Requirements

| Control | Impact |
|---------|--------|
| Email-as-name | Name is non-nullable string, can't be used for auth decisions (display only) |
| Optional fields | Backend MUST validate null vs blank consistently — name non-nullable guarantees invariants |

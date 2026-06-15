# simplified-registration Specification

## Purpose

Define the minimal registration flow: email+password only, with optional name/companyName for backward compatibility and email-as-name fallback for customers who skip those fields.

## Requirements

### R1: Registration Page (Frontend)

The registration page MUST show email and password as required fields. Name and companyName MAY be shown but MUST be marked as optional.

#### Scenario: Render minimal registration form

- GIVEN a new visitor at the register page
- WHEN the page loads
- THEN email and password fields are visible and required
- AND name and companyName fields are either hidden or clearly marked as optional

### R2: Backward-Compatible Registration

The system MUST accept registrations with all four fields (email, password, name, companyName) exactly as before.

#### Scenario: Full registration still works

- GIVEN a request with email, password, name, and companyName
- WHEN POST /api/v1/auth/register
- THEN 201 Created — backward compatible
- AND Customer created with the provided name and companyName

### R3: Email-as-Name Fallback

When name is omitted, the system MUST use the email prefix (characters before `@`) as the Customer's `name`.

#### Scenario: Name defaults to email prefix

- GIVEN a request without name
- WHEN POST /api/v1/auth/register with email "juan@example.com"
- THEN Customer.name is "juan"
- AND welcome email uses "juan" as greeting

### R4: Welcome Email Fallback

The welcome email template MUST use the email prefix as fallback when Customer name is an email placeholder.

#### Scenario: Greeting uses email prefix

- GIVEN a Customer registered without name (name = email prefix)
- WHEN the WELCOME email is rendered
- THEN the greeting displays the email prefix, e.g. "Hello juan!"

## Security Requirements

| Control | Mitigation |
|---------|------------|
| Email prefix leak | Name derived from email is display-only, not used for auth or authorization |
| Placeholder detection | Backend SHOULD distinguish real names from placeholders by checking if name matches email prefix pattern |

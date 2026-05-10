package com.siga.bdd.customerAuth

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class CustomerAuthBehaviorSpec : BehaviorSpec({
    // ─────────────────────────────────────────────────────────────────────────
    // R1: Customer Registration
    // ─────────────────────────────────────────────────────────────────────────

    given("a valid request (email, password, name, companyName)") {
        `When`("POST /api/v1/auth/register") {
            then("201 + { status: \"pending\" }") {
                pending { }
            }
            then("Customer created with BCrypt password hash, isActive=false") {
                pending { }
            }
            then("verification email sent") {
                pending { }
            }
        }
    }

    given("existing Customer with email \"a@b.com\"") {
        `When`("POST /api/v1/auth/register with email \"a@b.com\"") {
            then("409 Conflict") {
                pending { }
            }
        }
    }

    given("request without email/password/name/companyName") {
        `When`("POST /api/v1/auth/register") {
            then("400 Bad Request") {
                pending { }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // R2: Email Verification
    // ─────────────────────────────────────────────────────────────────────────

    given("a pending Customer with valid verification token") {
        `When`("GET /api/v1/auth/verify?token=valid-token") {
            then("200 + Customer isActive=true, token invalidated") {
                pending { }
            }
        }
    }

    given("a verification token older than 24h") {
        `When`("GET /api/v1/auth/verify?token=expired-token") {
            then("410 Gone") {
                pending { }
            }
        }
    }

    given("a non-existent verification token") {
        `When`("GET /api/v1/auth/verify?token=invalid-token") {
            then("404 Not Found") {
                pending { }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // R3: Login
    // ─────────────────────────────────────────────────────────────────────────

    given("an active Customer with valid credentials") {
        `When`("POST /api/v1/auth/login") {
            then("200 + JWT with tenantId=customer.id, principalType=customer, rol") {
                pending { }
            }
        }
    }

    given("an active User (customerId=1) with valid credentials") {
        `When`("POST /api/v1/auth/login") {
            then("200 + JWT with tenantId=1, principalType=user") {
                pending { }
            }
        }
    }

    given("a Customer with isActive=false") {
        `When`("POST /api/v1/auth/login with valid credentials") {
            then("403 Forbidden") {
                pending { }
            }
        }
    }

    given("any registered principal") {
        `When`("POST /api/v1/auth/login with wrong password") {
            then("401 Unauthorized (generic, no principal disclosure)") {
                pending { }
            }
        }
    }

    given("no principal exists with the given email") {
        `When`("POST /api/v1/auth/login") {
            then("401 Unauthorized") {
                pending { }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // R4: User Creation (Tenant Scoped)
    // ─────────────────────────────────────────────────────────────────────────

    given("an authenticated Customer (JWT: tenantId=1, principalType=customer)") {
        `When`("POST /api/v1/auth/users with valid user data") {
            then("201 + User created with customerId=1, BCrypt password") {
                pending { }
            }
        }
    }

    given("no JWT token") {
        `When`("POST /api/v1/auth/users") {
            then("401 Unauthorized") {
                pending { }
            }
        }
    }

    given("a JWT with principalType=user") {
        `When`("POST /api/v1/auth/users") {
            then("403 Forbidden") {
                pending { }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // R5: User Listing (Tenant Scoped)
    // ─────────────────────────────────────────────────────────────────────────

    given("an authenticated Customer (tenantId=1) with 3 users under customerId=1") {
        `When`("GET /api/v1/auth/users") {
            then("200 + only users with customerId=1 returned") {
                pending { }
            }
        }
    }

    given("an authenticated Customer (tenantId=2) with no users") {
        `When`("GET /api/v1/auth/users") {
            then("200 + empty list") {
                pending { }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // R6: Verification Token Expiry (NFR-2)
    // ─────────────────────────────────────────────────────────────────────────

    given("a verification token created 25 hours ago") {
        `When`("GET /api/v1/auth/verify?token=old-token") {
            then("410 Gone") {
                pending { }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // R7: Backward Compatibility (NFR-1)
    // ─────────────────────────────────────────────────────────────────────────

    given("a publicly accessible endpoint (e.g., health check)") {
        `When`("an unauthenticated GET request is made") {
            then("200 OK — no auth required") {
                pending { }
            }
        }
    }
})

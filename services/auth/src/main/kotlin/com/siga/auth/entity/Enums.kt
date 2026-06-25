package com.siga.auth.entity

/**
 * Operational roles of the SaaS system.
 * Each user has exactly one role that defines their base permissions.
 */
enum class UserRole {
    OWNER,
    ADMINISTRATOR,
    OPERATOR,
    CASHIER,
    EMPLOYEE
}

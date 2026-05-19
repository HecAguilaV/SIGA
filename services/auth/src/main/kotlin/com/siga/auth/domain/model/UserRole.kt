package com.siga.auth.domain.model

/**
 * Pure domain enum for user roles.
 * Mirrors com.siga.auth.entity.UserRole for hexagonal isolation.
 */
enum class UserRole {
    ADMINISTRATOR,
    OPERATOR,
    CASHIER,
    EMPLOYEE
}

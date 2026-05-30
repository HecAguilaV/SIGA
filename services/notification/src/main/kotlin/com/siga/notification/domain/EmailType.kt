package com.siga.notification.domain

/**
 * Types of email events that the Notification service can handle.
 */
enum class EmailType {
    /** Sent when a new customer registers and needs email verification */
    WELCOME,
    /** Sent when a customer requests a password reset */
    PASSWORD_RESET
}

package com.siga.auth.entity

/**
 * Roles operativos del sistema SaaS.
 * Cada usuario tiene exactamente un rol que define sus permisos base.
 */
enum class Rol {
    ADMINISTRADOR,
    OPERADOR,
    CAJERO
}

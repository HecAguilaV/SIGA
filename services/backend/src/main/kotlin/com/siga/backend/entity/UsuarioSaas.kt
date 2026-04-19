package com.siga.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "USUARIOS", schema = "siga_saas")
class UsuarioSaas(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    @Column(name = "password_hash", nullable = false, length = 255)
    var passwordHash: String,

    @Column(nullable = false, length = 100)
    var nombre: String,

    @Column(length = 100)
    var apellido: String? = null,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var rol: Rol,

    @Column(name = "usuario_comercial_id")
    var usuarioComercialId: Int? = null,

    @Column(nullable = false)
    var activo: Boolean = true,

    @Column(name = "fecha_creacion", nullable = false)
    var fechaCreacion: Instant = Instant.now(),

    @Column(name = "fecha_actualizacion", nullable = false)
    var fechaActualizacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UsuarioSaas) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "UsuarioSaas(id=$id, email=$email, rol=$rol)"

    fun copy(
        id: Int = this.id,
        email: String = this.email,
        passwordHash: String = this.passwordHash,
        nombre: String = this.nombre,
        apellido: String? = this.apellido,
        rol: Rol = this.rol,
        usuarioComercialId: Int? = this.usuarioComercialId,
        activo: Boolean = this.activo,
        fechaCreacion: Instant = this.fechaCreacion,
        fechaActualizacion: Instant = this.fechaActualizacion
    ): UsuarioSaas = UsuarioSaas(id, email, passwordHash, nombre, apellido, rol, usuarioComercialId, activo, fechaCreacion, fechaActualizacion)
}

enum class Rol {
    ADMINISTRADOR,
    OPERADOR,
    CAJERO
}
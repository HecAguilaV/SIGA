package com.siga.auth.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "USUARIOS", schema = "siga_comercial")
class UsuarioComercial(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    @Column(name = "password_hash", nullable = false, length = 255)
    var passwordHash: String,

    @Column(nullable = false, length = 100)
    var nombre: String,

    @Column(length = 100)
    var apellido: String? = null,

    @Column(length = 20)
    var rut: String? = null,

    @Column(length = 20)
    var telefono: String? = null,

    @Column(name = "nombre_empresa", length = 255)
    var nombreEmpresa: String? = null,

    @Column(nullable = false)
    var activo: Boolean = true,

    @Column(name = "en_trial", nullable = false)
    var enTrial: Boolean = false,

    @Column(name = "fecha_inicio_trial")
    var fechaInicioTrial: Instant? = null,

    @Column(name = "fecha_fin_trial")
    var fechaFinTrial: Instant? = null,

    @Column(length = 20)
    var rol: String = "cliente",

    @Column(name = "plan_id")
    var planId: Int? = null,

    @Column(name = "fecha_creacion", nullable = false)
    val fechaCreacion: Instant = Instant.now(),

    @Column(name = "fecha_actualizacion", nullable = false)
    var fechaActualizacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UsuarioComercial) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "UsuarioComercial(id=$id, email=$email, empresa=$nombreEmpresa)"
}

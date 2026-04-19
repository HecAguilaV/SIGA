package com.siga.inventario.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "LOCALES", schema = "siga_saas")
class Local(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false, length = 255)
    var nombre: String,

    @Column(columnDefinition = "TEXT")
    var direccion: String? = null,

    @Column(length = 100)
    var ciudad: String? = null,

    @Column(nullable = false)
    var activo: Boolean = true,

    @Column(name = "usuario_comercial_id")
    val usuarioComercialId: Int? = null,

    @Column(name = "fecha_creacion", nullable = false)
    val fechaCreacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Local) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Local(id=$id, nombre=$nombre, ciudad=$ciudad)"
}

package com.siga.inventario.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "CATEGORIAS", schema = "siga_saas")
class Categoria(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false, unique = true, length = 100)
    var nombre: String,

    @Column(columnDefinition = "TEXT")
    var descripcion: String? = null,

    @Column(nullable = false)
    var activa: Boolean = true,

    @Column(name = "usuario_comercial_id")
    val usuarioComercialId: Int? = null,

    @Column(name = "fecha_creacion", nullable = false)
    val fechaCreacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Categoria) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Categoria(id=$id, nombre=$nombre)"
}

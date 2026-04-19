package com.siga.auth.entity

import jakarta.persistence.*
import java.io.Serializable

@Embeddable
class UsuarioLocalId(
    @Column(name = "usuario_id", nullable = false)
    var usuarioId: Int = 0,

    @Column(name = "local_id", nullable = false)
    var localId: Int = 0
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UsuarioLocalId) return false
        return usuarioId == other.usuarioId && localId == other.localId
    }

    override fun hashCode(): Int = 31 * usuarioId.hashCode() + localId.hashCode()
}

@Entity
@Table(name = "USUARIOS_LOCALES", schema = "siga_saas")
class UsuarioLocal(
    @EmbeddedId
    val id: UsuarioLocalId = UsuarioLocalId(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    val usuario: UsuarioSaas? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UsuarioLocal) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "UsuarioLocal(usuarioId=${id.usuarioId}, localId=${id.localId})"
}

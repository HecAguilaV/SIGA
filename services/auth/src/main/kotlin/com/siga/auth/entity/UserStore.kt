package com.siga.auth.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.Instant

@Embeddable
class UserStoreId(
    @Column(name = "user_id", nullable = false)
    var userId: Int = 0,

    @Column(name = "store_id", nullable = false)
    var storeId: Int = 0
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserStoreId) return false
        return userId == other.userId && storeId == other.storeId
    }

    override fun hashCode(): Int = 31 * userId.hashCode() + storeId.hashCode()
}

@Entity
@Table(name = "user_stores", schema = "auth")
class UserStore(
    @EmbeddedId
    val id: UserStoreId = UserStoreId(),

    @Column(name = "assigned_at", nullable = false)
    val assignedAt: Instant = Instant.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    val user: User? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserStore) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "UserStore(userId=${id.userId}, storeId=${id.storeId})"
}

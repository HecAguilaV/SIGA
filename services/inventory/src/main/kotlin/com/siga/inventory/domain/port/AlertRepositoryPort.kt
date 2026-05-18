package com.siga.inventory.domain.port

import com.siga.inventory.domain.model.Alert
import java.util.UUID

/**
 * Port (Hexagonal Architecture) for Alert persistence.
 *
 * The Domain/Application layers depend on this interface,
 * NOT on JPA or the Alert entity.
 */
interface AlertRepositoryPort {
    fun save(alert: Alert): Alert
    fun findByStoreId(storeId: UUID): List<Alert>
}

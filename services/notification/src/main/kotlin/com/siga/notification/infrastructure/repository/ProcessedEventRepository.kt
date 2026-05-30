package com.siga.notification.infrastructure.repository

import com.siga.notification.infrastructure.entity.ProcessedEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for idempotency tracking of processed email events.
 */
@Repository
interface ProcessedEventRepository : JpaRepository<ProcessedEvent, UUID>

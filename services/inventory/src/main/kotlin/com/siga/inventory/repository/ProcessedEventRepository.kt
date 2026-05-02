package com.siga.inventory.repository

import com.siga.inventory.entity.ProcessedEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for idempotency tracking of processed Kafka events.
 */
@Repository
interface ProcessedEventRepository : JpaRepository<ProcessedEvent, UUID>

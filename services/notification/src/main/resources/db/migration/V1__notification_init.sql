-- SIGA - Notification Service Initialization (Flyway V1)
-- Creates the notification schema and idempotency tracking table.
-- All DDL is managed by Flyway; init-db.sh only creates the schema + user.

CREATE SCHEMA IF NOT EXISTS notification;
SET search_path TO notification;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS notification.processed_events (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

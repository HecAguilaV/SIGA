package com.siga.inventory.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration

/**
 * Enables Spring's annotation-driven cache management.
 *
 * WHY: [@EnableCaching] activates the post-processor that scans for [org.springframework.cache.annotation.Cacheable],
 * [org.springframework.cache.annotation.CacheEvict], and other cache annotations. Without this, [@Cacheable]
 * on [com.siga.inventory.application.usecase.ConsolidatedStockUseCase] would be silently ignored.
 *
 * The Redis cache backend is auto-configured by `spring-boot-starter-data-redis` and customised
 * via `application.yml` (host, port, TTL). Spring Boot's [RedisCacheConfiguration] provides sensible
 * defaults: JdkSerializationRedisSerializer, no prefix, key/value serialization via Lettuce.
 */
@Configuration
@EnableCaching
class CacheConfig

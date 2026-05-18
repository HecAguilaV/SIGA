package com.siga.inventory.config

import org.hibernate.boot.MetadataBuilder
import org.hibernate.boot.spi.MetadataBuilderContributor
import org.hibernate.dialect.function.StandardSQLFunction
import org.hibernate.type.StandardBasicTypes

/**
 * Registers the PostgreSQL `f_unaccent` function with Hibernate's JPQL dialect.
 *
 * WHY: Without registration, Hibernate cannot determine the return type of
 * `f_unaccent()` in JPQL queries. This causes a `SemanticException` when using
 * the function in `ILIKE` comparisons because the operand type resolves to
 * `java.lang.Object` instead of `String`.
 *
 * The function itself is created by the V2 Flyway migration as an IMMUTABLE
 * wrapper around PostgreSQL's `unaccent()` extension.
 *
 * For H2 test compatibility, the `@Sql` script creates a matching alias.
 */
class UnaccentMetadataContributor : MetadataBuilderContributor {
    override fun contribute(builder: MetadataBuilder) {
        builder.applySqlFunction(
            "f_unaccent",
            StandardSQLFunction("f_unaccent", StandardBasicTypes.STRING)
        )
    }
}

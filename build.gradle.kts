plugins {
    id("jacoco")
    kotlin("jvm") version "2.1.10" apply false
    kotlin("plugin.spring") version "2.1.10" apply false
    kotlin("plugin.jpa") version "2.1.10" apply false
    id("org.springframework.boot") version "3.4.3" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

repositories {
    mavenCentral()
}

jacoco {
    toolVersion = "0.8.12"
}

/**
 * Aggregated JaCoCo coverage report across all subprojects.
 *
 * Combines coverage from every leaf subproject into a single HTML + XML report.
 *
 * Usage:
 *   ./gradlew test             — run ALL tests first (may fail on agent/billing)
 *   ./gradlew test :services:auth:test jacocoAggregatedReport  — single command
 *   ./gradlew jacocoAggregatedReport       — aggregate from previous test runs only
 *
 * Report: build/reports/jacoco/aggregated/html/index.html
 */

val leafProjects = subprojects.filter { it.childProjects.isEmpty() }

tasks.register<JacocoReport>("jacocoAggregatedReport") {
    group = "Reporting"
    description = "Generates an aggregated JaCoCo coverage report across all subprojects."

    leafProjects.forEach { project ->
        dependsOn("${project.path}:compileKotlin")
    }

    // Use lazy providers so Gradle doesn't track task dependencies by file output
    // (we want to consume existing exec files without forcing test tasks to run)
    executionData.setFrom(
        leafProjects.map { provider { file("${it.buildDir}/jacoco/test.exec") } }
    )

    sourceDirectories.setFrom(
        leafProjects.map { file("${it.projectDir}/src/main/kotlin") }
    )

    classDirectories.setFrom(
        leafProjects.map { provider { fileTree("${it.buildDir}/classes/kotlin/main") } }
    )

    reports {
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/aggregated/html"))
        xml.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/aggregated/xml/jacoco.xml"))
        csv.required.set(false)
    }

    doFirst {
        val execFiles = executionData.files.filter { it.exists() }
        if (execFiles.isEmpty()) {
            throw GradleException(
                "No JaCoCo execution data found. Run tests first:\n" +
                "  ./gradlew test jacocoAggregatedReport"
            )
        }
        logger.lifecycle("Aggregating ${execFiles.size} execution data file(s):")
        execFiles.forEach { logger.lifecycle("  - ${it.name} (${it.parentFile.parentFile.name})") }
    }
}

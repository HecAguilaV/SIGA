import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("jacoco")
}

group = "com.siga"
version = "1.0-SNAPSHOT"
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2025.1.0"

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:4.0.6")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

dependencies {
    // Spring Boot WebFlux (for SSE and reactive endpoints)
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Eureka client for service discovery
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Actuator for health checks
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // JDBC for FallbackEngine — added in Phase 3
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.postgresql:postgresql")

    // Google GenAI SDK for Gemini API calls
    implementation("com.google.genai:google-genai:1.43.0")

    // Jackson Kotlin module
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.11")
    testRuntimeOnly("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}

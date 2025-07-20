plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
}

group = "com.ethyllium"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2024.0.1"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-webflux") {
        implementation("org.springframework.boot:spring-boot-starter-aop")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-reactor-netty")
    compileOnly("jakarta.servlet:jakarta.servlet-api")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-mail")

    //---

    // Database & Data Access
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc") // If using R2DBC
    implementation("org.postgresql:r2dbc-postgresql")

    //---

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("io.lettuce:lettuce-core:6.6.0.RELEASE")     // Lettuce client
    implementation("org.redisson:redisson:3.50.0")     // Redisson client

    //---

    // Spring Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
    implementation("io.micrometer:micrometer-registry-prometheus:1.15.1")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    //---

    // Kotlin Specific & Reactive Programming
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")     // Jackson support for Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")     // Kotlin reflection
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")     // Kotlin Coroutines
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.3")     // Reactor Kotlin extensions
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.1")     // Coroutines integration with Reactor

    //---

    // Security & Authentication
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")     // Thymeleaf Spring Security integration
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")     // JWT API
    implementation("io.jsonwebtoken:jjwt-impl:0.12.6")     // JWT implementation
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")     // JWT Jackson support
    implementation("com.warrenstrange:googleauth:1.5.0")     // Google Authenticator (2FA)

    //---

    // Utilities & Helper Libraries
    implementation("org.springframework.retry:spring-retry:2.0.11")     // Spring Retry
    implementation("commons-validator:commons-validator:1.9.0")     // Commons Validator
    implementation("commons-codec:commons-codec:1.18.0")     // Commons Codec
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.0")     // Caffeine cache

    //---

    // QR Code Generation
    implementation("com.google.zxing:core:3.5.3")     // ZXing core
    implementation("com.google.zxing:javase:3.5.3")     // ZXing Java SE extensions

    //---

    // Third-party Integrations
    implementation("com.twilio.sdk:twilio:10.7.0")     // Twilio for SMS/voice

    //---

    // API Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.8.8")

    // Test Dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.projectreactor:reactor-test")     // Reactor testing utilities
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")     // JUnit Platform Launcher
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

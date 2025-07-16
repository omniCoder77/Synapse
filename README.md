# Synapse: A Resilient, Event-Driven E-Commerce Platform
Synapse is a sophisticated, backend-driven e-commerce platform built on a fully asynchronous, reactive microservices' architecture. It leverages **Kotlin**, the **Spring Ecosystem**, and modern, event-driven patterns to create a scalable, fault-tolerant, and high-performance system.

This project is not just a standard e-commerce application; it's a showcase of advanced software engineering principles, including **CQRS**, **Event Sourcing with an Outbox Pattern**, polyglot persistence, and **secure-by-design** development.

# 🚀 Live Demo & API Documentation
A consolidated API documentation for all services is exposed through the API Gateway via Swagger UI.

* Swagger UI (API Docs): http://localhost:8080/swagger-ui.html
* API Gateway Entrypoint: http://localhost:8080

*(Note: The links above are for the local development environment.)*
# 🏛️ System Architecture
Synapse is composed of several independent microservices that communicate through a combination of synchronous (gRPC, REST) and asynchronous (Kafka) protocols. This design ensures loose coupling, high availability, and independent scalability of each component.
``` mermaid
graph TB
    %% ===== STYLING =====
    classDef client fill:#667eea,stroke:#764ba2,stroke-width:3px,color:#fff,font-weight:bold
    classDef gateway fill:#f093fb,stroke:#f5576c,stroke-width:3px,color:#fff,font-weight:bold
    classDef service fill:#4facfe,stroke:#00f2fe,stroke-width:2px,color:#fff,font-weight:bold
    classDef data fill:#43e97b,stroke:#38f9d7,stroke-width:2px,color:#fff,font-weight:bold
    classDef messaging fill:#fa709a,stroke:#fee140,stroke-width:2px,color:#fff,font-weight:bold
    classDef external fill:#a8edea,stroke:#fed6e3,stroke-width:2px,color:#333,font-weight:bold
    classDef legend fill:#f8f9fa,stroke:#dee2e6,stroke-width:1px,color:#495057
    
    %% ===== LEGEND =====
    subgraph L[" 📋 LEGEND "]
        L1["🌐 REST API"] 
        L2["⚡ gRPC"]
        L3["📨 Kafka Events"]
        L4["💾 Database"]
        L5["📡 Service Discovery"]
    end
    class L1,L2,L3,L4,L5 legend
    
    %% ===== TIER 1: CLIENT =====
    subgraph T1[" 👥 CLIENT TIER "]
        C1[📱 Mobile App]
        C2[💻 Web App]
        C3[🖥️ Admin Panel]
    end
    class C1,C2,C3 client
    
    %% ===== TIER 2: GATEWAY =====
    subgraph T2[" 🚪 GATEWAY TIER "]
        GW[🔗 API Gateway<br/>• Load Balancing<br/>• Rate Limiting<br/>• Authentication]
        SD[🗺️ Service Discovery<br/>Eureka Server]
    end
    class GW gateway
    class SD service
    
    %% ===== TIER 3: MICROSERVICES =====
    subgraph T3[" 🏗️ MICROSERVICES TIER "]
        direction TB
        subgraph CORE[" Core Services "]
            AS[🔐 Auth Service<br/>• JWT Tokens<br/>• User Management<br/>• SMS OTP]
            PS[📦 Product Service<br/>• Catalog<br/>• Inventory<br/>• Categories]
            OS[🧾 Order Service<br/>• Cart Management<br/>• Order Processing<br/>• Status Tracking]
        end
        
        subgraph SUPPORT[" Support Services "]
            PayS[💳 Payment Service<br/>• Payment Processing<br/>• Wallet Management<br/>• Refunds]
            SS[🔍 Search Service<br/>• Full-text Search<br/>• Recommendations<br/>• Filters]
        end
    end
    class AS,PS,OS,PayS,SS service
    
    %% ===== TIER 4: MESSAGE BUS =====
    subgraph T4[" 📡 EVENT STREAMING "]
        KB[⚡ Kafka Event Bus<br/>• Product Events<br/>• Order Events<br/>• Payment Events<br/>• User Events]
    end
    class KB messaging
    
    %% ===== TIER 5: DATA LAYER =====
    subgraph T5[" 💾 DATA PERSISTENCE "]
        direction LR
        subgraph MAIN_DB[" Primary Databases "]
            PG1[(🐘 PostgreSQL<br/>Auth & Users)]
            MG1[(🍃 MongoDB<br/>Products & Catalog)]
            PG2[(🐘 PostgreSQL<br/>Orders & Transactions)]
            PG3[(🐘 PostgreSQL<br/>Payments & Wallet)]
        end
        
        subgraph CACHE_SEARCH[" Cache & Search "]
            RD[(⚡ Redis<br/>Session & Cache)]
            ES[(🔍 Elasticsearch<br/>Search Index)]
        end
    end
    class PG1,MG1,PG2,PG3,RD,ES data
    
    %% ===== TIER 6: EXTERNAL =====
    subgraph T6[" 🌐 EXTERNAL SERVICES "]
        TW[💬 Twilio<br/>SMS Gateway]
        RZ[💵 Razorpay<br/>Payment Gateway]
    end
    class TW,RZ external
    
    %% ===== CLIENT CONNECTIONS =====
    C1 --> GW
    C2 --> GW
    C3 --> GW
    
    %% ===== GATEWAY CONNECTIONS =====
    GW --> AS
    GW --> PS
    GW --> OS
    GW --> PayS
    GW --> SS
    
    %% ===== SERVICE DISCOVERY =====
    AS -.-> SD
    PS -.-> SD
    OS -.-> SD
    PayS -.-> SD
    SS -.-> SD
    
    %% ===== INTER-SERVICE COMMUNICATION =====
    OS --> PS
    OS --> PayS
    PS --> AS
    PayS --> AS
    
    %% ===== EVENT STREAMING =====
    PS --> KB
    OS --> KB
    PayS --> KB
    AS --> KB
    KB --> SS
    
    %% ===== DATA CONNECTIONS =====
    AS --> PG1
    AS --> RD
    PS --> MG1
    OS --> PG2
    PayS --> PG3
    SS --> ES
    
    %% ===== EXTERNAL CONNECTIONS =====
    AS --> TW
    PayS --> RZ
    
    %% ===== STYLING APPLICATIONS =====
    style T1 fill:#f8f9fa,stroke:#6c757d,stroke-width:2px,color:#495057
    style T2 fill:#f8f9fa,stroke:#6c757d,stroke-width:2px,color:#495057
    style T3 fill:#f8f9fa,stroke:#6c757d,stroke-width:2px,color:#495057
    style T4 fill:#f8f9fa,stroke:#6c757d,stroke-width:2px,color:#495057
    style T5 fill:#f8f9fa,stroke:#6c757d,stroke-width:2px,color:#495057
    style T6 fill:#f8f9fa,stroke:#6c757d,stroke-width:2px,color:#495057
    style L fill:#f8f9fa,stroke:#6c757d,stroke-width:2px,color:#495057
    style CORE fill:#e3f2fd,stroke:#1976d2,stroke-width:1px,color:#1976d2
    style SUPPORT fill:#e8f5e9,stroke:#388e3c,stroke-width:1px,color:#388e3c
    style MAIN_DB fill:#fff3e0,stroke:#f57c00,stroke-width:1px,color:#f57c00
    style CACHE_SEARCH fill:#fce4ec,stroke:#c2185b,stroke-width:1px,color:#c2185b
```

# ✨ Key Features & Service Breakdown
# ✨ Key Features & Service Breakdown

| Service                | Core Responsibilities & Features                                                                                                                                                                                                 | Key Technologies                                                   | Status       |
|------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------|--------------|
| **API Gateway**        | • Single entry point for all clients.<br>• Dynamic routing & service discovery.<br>• Centralized authentication filter.<br>• Rate limiting and Circuit Breaker patterns for resilience.                                          | Spring Cloud Gateway, WebFlux, <br>Resilience4j                    | ✅ Functional |
| **🔐 Auth Service**    | • Secure user registration & JWT-based authentication.<br>• MFA with TOTP (Google Authenticator) & QR code generation.<br>• Password reset and email verification flows.<br>• Secure credential storage & device fingerprinting. | Spring Security, R2DBC, JWT, Twilio, <br>Quartz, PostgreSQL, Redis | ✅ Functional |
| **📦 Product Service** | • Comprehensive product catalog management (CRUD).<br>• Rich product model with variants, SEO, media, and specifications.<br>• Role-based access control (RBAC) for sellers & admins.                                            | Spring Data MongoDB, MongoDB                                       | ✅ Functional |
| **🛍️ Order Service**  | • Manages the complete order lifecycle.<br>• Validates product availability via gRPC calls to the Product Service.<br>• Publishes `OrderCreated` events to Kafka.                                                                | Spring Data JPA, gRPC, Kafka, <br>PostgreSQL                       | ✅ Functional |
| **💳 Payment Service** | • Integration with **Razorpay** for order creation and payment processing.<br>• Secure webhook handling with signature verification.<br>• Implements the **Transactional Outbox Pattern** for reliable event publishing.         | Spring Data JPA, Kafka, <br>Razorpay API, PostgreSQL               | ✅ Functional |
| **🔍 Search Service**  | • Provides advanced, full-text product search.<br>• Consumes product events from Kafka to keep the search index synchronized.<br>• Offers filtering, sorting, and autocomplete suggestions.                                      | Spring Data Elasticsearch, <br>Elasticsearch                       | ✅ Functional |
# 💡 Technical Highlights & Design Patterns

This project goes beyond a simple implementation and showcases a deep understanding of modern backend engineering.

* **Event-Driven Architecture with Transactional Outbox**: The `payment-service` uses the **Outbox Pattern** to guarantee "at-least-once" delivery of critical business events. Events are written to a local database table within the same transaction as the business operation and then reliably published to Kafka by a separate process. This ensures data consistency across microservices, even in the event of publisher failure.
* **Polyglot Persistence**: The architecture deliberately uses different database technologies, each chosen for its strengths in handling a specific type of data:
    * **PostgreSQL**: For transactional, relational data in the `Auth`, `Order`, and `Payment` services.
    * **MongoDB**: For the flexible, document-based structure of the product catalog in the `Product Service`.
    * **Elasticsearch**: For powerful, fast, and complex search queries in the `Search Service`.
    * **Redis**: For caching, rate limiting, and managing ephemeral state like OTPs and sessions.
* **Reactive & Asynchronous Core**: Built from the ground up with a non-blocking stack (**Spring WebFlux**, **Project Reactor**, **R2DBC**) to handle high concurrency with efficient resource utilization, essential for a responsive e-commerce platform.
* **Secure by Design**: Security is a cornerstone of the platform, with features including:
    * **Centralized Authentication**: The API Gateway enforces JWT validation for all protected routes.
    * **Role-Based Access Control (RBAC)**: Fine-grained permissions are enforced at the controller level (e.g. `@RequiresRoles({"SELLER"})`).
    * **Secure Webhooks**: Payloads from Razorpay are verified using HMAC-SHA256 signatures to prevent tampering.
    * **MFA and Secure Credentials**: Strong password hashing (BCrypt), TOTP, and secure key management using a Java KeyStore.
* **High-Performance Inter-Service Communication**: The system uses a mix of communication styles:
    * **gRPC**: For low-latency, synchronous communication where a direct response is needed (e.g., `Order Service` validating products with `Product Service`).
    * **Kafka**: For asynchronous, event-driven communication to decouple services and improve resilience.
    * **REST/HTTP**: For external client-facing APIs.
# 🛠️ Tech Stack
| Category                     | Technologies                                                                                                                |
|------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| **Languages and Frameworks** | `Kotlin, Spring Boot, Spring Cloud (Gateway, OpenFeign), Spring Data (JPA, MongoDB, Elasticsearch, R2DBC), Spring Security` |
| Databases                    | 	PostgreSQL, MongoDB, Elasticsearch, Redis                                                                                  |
 | Communication                | 	RESTful APIs, gRPC, Apache Kafka, WebSockets (STOMP)                                                                       |
 | Authentication               | 	JWT, MFA/TOTP, BCrypt, Java KeyStore (JCEKS)                                                                               |
 | External APIs                | 	Razorpay, Twilio                                                                                                           |
 | DevOps & Tooling             | 	Gradle, Docker, Swagger/OpenAPI, Ehcache                                                                                   |
# 🚀 Getting Started
The entire platform can be run locally using Docker and Docker Compose.
Prerequisites
* Java 17 or higher
* Docker & Docker Compose

Local Setup
1. Environment Variables: Create a `.env` file in the root of the project by copying the `example.env` file. Populate it with your credentials for external services like Razorpay and Twilio.
2. Build the Project: Build all the service modules to create the necessary JAR files.
   ``` bash
   ./gradlew clean build
   ```
3. Run with Docker Compose: Launch all the services and backing infrastructure (databases, Kafka, etc.) using Docker Compose.
   ```bash
   docker-compose up -d
   ```
4. **Accessing Services:**
    * **API Gateway:** `http://localhost:8080`
    * **Swagger UI:** `http://localhost:8080/swagger-ui.html`
    * Individual services can also be accessed on their respective ports if needed.

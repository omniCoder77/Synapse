# E-Commerce Backend System

A high-performance, scalable e-commerce backend built with modern architectural patterns like hexagonal architecture and technologies like kafka.

## 🚀 Technologies Used

- **Languages**: Kotlin, Java
- **Frameworks**: Spring Boot, Spring Security, Spring WebFlux, Spring Data JPA
- **Architectures**: 
  - Microservices
  - Domain-Driven Design (DDD)
  - CQRS (Command Query Responsibility Segregation)
  - Hexagonal/Clean Architecture
  - Event-Driven Architecture
- **APIs**: RESTful, gRPC
- **Messaging**: Apache Kafka, RabbitMQ
- **Databases**: PostgreSQL, Redis, ScyllaDB/Cassandra
- **Search**: Elasticsearch
- **Other**: JWT Authentication, Feign Client, Kotlin Coroutines, Rate Limiting, Circuit Breakers

## 🌟 Features

- Scalable microservices architecture
- Event-driven order processing
- Real-time inventory management
- Distributed transaction handling
- High-performance search with Elasticsearch
- Multiple API protocols support (REST/gRPC)
- Resilient communication with circuit breakers and rate limiting
- CQRS pattern for optimized read/write operations
- JWT-based authentication and authorization

## 📦 System Architecture

The system follows a modular microservices architecture with:

1. **API Gateway**: Single entry point with routing, rate limiting, and authentication
2. **Core Services**:
   - Product Service (CRUD operations, inventory management)
   - Order Service (order processing, payment integration)
   - User Service (authentication, user profiles)
   - Search Service (powered by Elasticsearch)
   - Notification Service (emails, SMS)
   - Payment Service (uses Razorpay)
3. **Event Bus**: Kafka/RabbitMQ for inter-service communication
4. **Data Stores**: Polyglot persistence with each service using its optimal database

## 🛠️ Setup & Installation

### Prerequisites
- JDK 17+
- Docker (for containerized databases and message brokers)
- Gradle

### Running Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/ecommerce-backend.git
   ```
2. Start infrastructure services:
   ```bash
   docker-compose -f docker-compose-infra.yml up -d
   ```
3. Build and run services:
   ```bash
   ./gradlew build && ./gradlew bootRun
   ```
# 📚 API Documentation
API documentation is available via:

    Swagger UI: http://localhost:8080/swagger-ui.html

🤝 Contributing
Contributions are welcome! Please open an issue or submit a PR.

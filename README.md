# StayRide

StayRide is a backend system for **hotel booking and ride management** built with Java 21 and Spring Boot.

The project started as a **modular monolith** and was progressively evolved into a **microservices-based architecture** to demonstrate real-world backend and distributed-system concepts including authentication, caching, event-driven communication, service discovery, API gateways, concurrency, database locking, resilience, idempotency, and containerization.

---

## Architecture

The current architecture consists of:

- **StayRide Monolith** — user, hotel, room, booking, authentication, Kafka and transactional outbox functionality
- **Ride Service** — independently deployable ride and driver microservice with its own PostgreSQL database
- **API Gateway** — single client entry point, routing, load balancing and rate limiting
- **Eureka Service Discovery** — service registration and discovery
- **PostgreSQL**
- **Redis**
- **Apache Kafka**
- **Docker / Docker Compose**

### High-Level Architecture

```text
                              Client
                                |
                                v
                     +----------------------+
                     |    API Gateway       |
                     |        :8080         |
                     +----------+-----------+
                                |
                         Eureka Discovery
                         +------+------+
                                |
                    +-----------+-----------+
                    |                       |
                    v                       v
          +-------------------+   +-------------------+
          | StayRide Monolith |   |   Ride Service    |
          |      :9999        |   |      :8081        |
          +---------+---------+   +---------+---------+
                    |                       |
                    v                       v
          +-------------------+   +-------------------+
          | StayRide Database |   |   Ride Database   |
          |    PostgreSQL     |   |    PostgreSQL     |
          +-------------------+   +-------------------+

                    |                       |
                    +-----------+-----------+
                                |
                         +------+------+
                         |             |
                         v             v
                      Redis          Kafka
                                      |
                                      v
                              Booking Events
```

---

# Repository Structure

```text
StayRide/
│
├── stayride-monolith/
│   ├── user/
│   ├── hotel/
│   ├── booking/
│   ├── common/
│   │   ├── security/
│   │   ├── exception/
│   │   ├── kafka/
│   │   └── outbox/
│   ├── Dockerfile
│   └── docker-compose.yaml
│
├── ride-service/
│   ├── client/
│   ├── config/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   ├── service/
│   ├── exception/
│   └── docker-compose.yaml
│
├── api-gateway/
│   ├── config/
│   └── src/
│
├── service-discovery/
│   └── src/
│
└── README.md
```

Each application is an independent Spring Boot project.

---

# Services

## 1. StayRide Monolith

The original StayRide application containing the core hotel-booking functionality.

### Responsibilities

- User management
- JWT authentication
- Role-based authorization
- Hotel management
- Room management
- Hotel booking
- Booking cancellation
- Booking events
- Redis caching
- Kafka integration
- Transactional Outbox

### Main Modules

```text
stayride-monolith/
├── user/
├── hotel/
├── booking/
└── common/
    ├── security/
    ├── exception/
    ├── kafka/
    └── outbox/
```

The application remains a monolith at the deployment boundary, while the code is organized into business modules.

---

## 2. Ride Service

Ride functionality was extracted from the original application into an independently deployable microservice.

### Responsibilities

- Driver management
- Driver availability
- Ride booking
- Driver allocation
- Ride lifecycle
- Ride history
- Concurrency control
- Resilient communication with the User API
- Idempotent ride creation

The Ride Service owns a separate PostgreSQL database.

```text
Ride Service
    |
    +-- Driver
    |
    +-- RideDetails
    |
    +-- Ride PostgreSQL Database
```

The Ride Service does not maintain a JPA relationship with the User entity in the monolith. It retrieves user information through service-to-service communication.

---

# API Gateway

The API Gateway provides a single entry point for client requests.

```text
Client
   |
   v
API Gateway :8080
   |
   +----> StayRide Monolith
   |
   +----> Ride Service
```

### Responsibilities

- Request routing
- Service discovery integration
- Load balancing
- Gateway-level rate limiting

### Routes

```text
/api/auth/**       -> StayRide Monolith
/api/users/**      -> StayRide Monolith
/api/hotels/**     -> StayRide Monolith
/api/bookings/**   -> StayRide Monolith

/api/rides/**      -> Ride Service
/api/drivers/**    -> Ride Service
```

---

# Service Discovery

StayRide uses **Netflix Eureka** for service discovery.

```text
                 +----------------------+
                 |   Eureka Server      |
                 |       :8761          |
                 +----------+-----------+
                            |
                   +--------+--------+
                   |                 |
                   v                 v
          StayRide Monolith     Ride Service
```

Services register themselves with Eureka.

The API Gateway and service-to-service communication can use logical service names rather than hardcoded service instance addresses.

---

# Inter-Service Communication

The Ride Service communicates with the StayRide Monolith to retrieve user information.

```text
Ride Service
     |
     | REST
     v
User API
     |
     v
StayRide Monolith
```

The communication uses:

- Spring `RestClient`
- Spring Cloud LoadBalancer
- Eureka service discovery
- Configured connection/read timeouts

The Ride Service calls the logical service name:

```text
http://STAYRIDE-MONOLITH
```

rather than depending on a fixed service instance address.

---

# Authentication & Authorization

StayRide uses stateless **JWT authentication** with Spring Security.

```text
Login
  |
  v
AuthenticationManager
  |
  v
CustomUserDetailsService
  |
  v
User Database
  |
  v
JwtService
  |
  v
JWT
```

For protected requests:

```text
Bearer JWT
    |
    v
JwtAuthenticationFilter
    |
    v
SecurityContext
    |
    +-- User ID
    +-- Role
    |
    v
Controller
    |
    v
Service
```

The authenticated identity comes from the JWT. Business ownership is then validated against application data.

---

# Role-Based Authorization

The main application uses role-based access control.

| Role | Responsibilities |
|---|---|
| `CUSTOMER` | Book rooms and cancel their own bookings |
| `HOTEL_ADMIN` | Create and manage hotels and rooms they own |
| `ADMIN` | Administrative operations |

Driver and ride functionality is handled by the independent Ride Service rather than being part of the monolith's main role model.

---

# Hotel & Room Management

Hotel administrators can manage hotels and rooms they own.

```text
ADMIN
  |
  | creates
  v
HOTEL_ADMIN
  |
  | creates
  v
Hotel
  |
  | creates
  v
Room
```

Ownership authorization is performed in the service layer so that a hotel administrator cannot modify another administrator's hotel.

---

# Hotel Booking

Customers can book available rooms for a date range.

```text
Customer
   |
   v
Booking Request
   |
   v
Availability Check
   |
   v
Room
   |
   v
Booking
```

Booking cancellation verifies that the authenticated customer owns the booking.

---

# Redis Caching

Redis is used for caching frequently accessed hotel data.

```text
GET Hotel
    |
    v
Redis Cache
    |
    +---- Cache Hit ----> Return cached response
    |
    +---- Cache Miss
              |
              v
         PostgreSQL
              |
              v
         Store in Redis
              |
              v
         Return response
```

Cache entries are evicted when relevant hotel data is updated.

Booking availability is intentionally not treated as ordinary cached data because stale availability can lead to incorrect booking decisions.

---

# Apache Kafka

Kafka is used for asynchronous event-driven communication.

The booking event flow is:

```text
Booking Created
      |
      v
Transactional Outbox
      |
      v
Outbox Publisher
      |
      v
Kafka
      |
      v
Event Consumer
```

Example event:

```text
booking-created
```

Kafka decouples asynchronous event processing from the original booking request.

---

# Transactional Outbox Pattern

Booking creation uses the **Transactional Outbox Pattern** to address the database/message dual-write problem.

```text
                Database Transaction
                       |
              +--------+--------+
              |                 |
              v                 v
        Save Booking      Save OutboxEvent
                              PENDING
                                 |
                                 v
                         Scheduled Publisher
                                 |
                                 v
                               Kafka
                                 |
                                 v
                              Consumer
```

The booking and its outbox event are persisted in the same database transaction.

A scheduled publisher periodically retrieves pending events and publishes them to Kafka.

---

# Concurrency & Driver Allocation

Driver allocation contains a race-condition scenario when multiple ride requests attempt to allocate an available driver simultaneously.

The project explores:

- `Thread`
- `Runnable`
- `ExecutorService`
- Fixed thread pools
- Spring `ThreadPoolTaskExecutor`
- `Future`
- `CompletableFuture`
- `supplyAsync()`
- Parallel processing
- Race conditions
- `synchronized`
- Database locking

## Why Database Locking?

Java-level synchronization protects threads within a single JVM.

In a distributed system, multiple Ride Service instances can exist:

```text
Ride Service Instance 1
        |
        +-- Thread A
        |
        v
      Driver


Ride Service Instance 2
        |
        +-- Thread B
        |
        v
      Same Driver
```

A Java `synchronized` block cannot coordinate these separate JVMs.

Therefore, the database is used as the shared source of truth.

The Ride Service uses **pessimistic database locking** when selecting an available driver, preventing concurrent transactions from allocating the same driver.

---

# Idempotency

Ride booking supports an **Idempotency-Key** to prevent accidental duplicate ride creation when a client retries the same logical request.

```text
Client
  |
  | Idempotency-Key: UUID
  v
Ride Service
  |
  +---- Existing key ----> Return existing ride
  |
  +---- New key ---------> Create ride
```

The idempotency key is also protected by a database unique constraint.

A client should generate a new key for a new logical operation and reuse the same key when retrying that operation.

---

# Resilience

The Ride Service implements resilience patterns around its communication with the StayRide Monolith.

Implemented patterns include:

- Timeout
- Retry
- Exponential retry backoff
- Circuit Breaker
- Fallback
- Bulkhead
- Gateway rate limiting

## Timeout

The User API call has configured connection/read timeouts so a slow dependency does not block the Ride Service indefinitely.

## Retry

Transient communication failures can be retried with exponential backoff.

Retries are configured for the User API call, which is a read operation.

## Circuit Breaker

The circuit breaker prevents repeated calls to a failing dependency.

```text
                 Success
              +-----------+
              |           |
              v           |
           CLOSED --------+
              |
              | failure threshold exceeded
              v
             OPEN
              |
              | wait duration
              v
          HALF_OPEN
           /      \
      success    failure
         |          |
         v          v
      CLOSED      OPEN
```

## Fallback

The User Service call has a fallback path for dependency failures.

The fallback demonstrates graceful dependency failure handling. Business operations should still preserve valid business state rather than silently creating incorrect data.

## Bulkhead

The User Service dependency is protected by a concurrency limit so that too many simultaneous calls do not consume all available resources.

## Gateway Rate Limiting

The API Gateway applies Redis-backed request rate limiting to Ride and Driver routes.

```text
/api/rides/**
/api/drivers/**
```

Rate limiting helps protect the downstream Ride Service from excessive request bursts.

---

# Technology Stack

## Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- Jakarta Validation
- Lombok

## Microservices

- Spring Cloud Gateway
- Netflix Eureka
- Spring Cloud LoadBalancer
- Spring `RestClient`

## Data

- PostgreSQL
- Redis

## Messaging

- Apache Kafka
- Transactional Outbox Pattern

## Security

- JWT
- Spring Security
- Role-Based Access Control

## Resilience

- Timeout
- Retry
- Exponential Backoff
- Circuit Breaker
- Bulkhead
- Fallback
- Rate Limiting

## Concurrency

- Threads
- ExecutorService
- Thread Pools
- CompletableFuture
- `synchronized`
- Pessimistic Database Locking

## Infrastructure

- Docker
- Docker Compose
- Maven

---

# Architecture Evolution

StayRide was intentionally developed incrementally.

```text
Phase 1
Modular Monolith
       |
       v
Phase 2
JWT Authentication
       |
       v
Phase 3
Redis Caching
       |
       v
Phase 4
Apache Kafka
       |
       v
Phase 5
Transactional Outbox
       |
       v
Phase 6
Docker + Docker Compose
       |
       v
Phase 7
Ride Microservice
       |
       v
Phase 8
API Gateway
       |
       v
Phase 9
Eureka Service Discovery
       |
       v
Phase 10
Inter-Service Communication
       |
       v
Phase 11
Concurrency & Database Locking
       |
       v
Phase 12
Resilience Patterns
       |
       v
Phase 13
Testing & Observability
```

The architecture was evolved gradually rather than introducing microservice infrastructure from the beginning.

---

# Request Flow Examples

## Hotel Request

```text
Client
  |
  v
API Gateway :8080
  |
  v
Eureka
  |
  v
StayRide Monolith :9999
  |
  v
Hotel Module
  |
  +----> Redis
  |
  +----> PostgreSQL
```

## Ride Request

```text
Client
  |
  v
API Gateway :8080
  |
  v
Eureka
  |
  v
Ride Service :8081
  |
  +----> User API
  |
  +----> Ride PostgreSQL
  |
  +----> Driver Allocation
```

## Booking Event

```text
Client
  |
  v
API Gateway
  |
  v
StayRide Monolith
  |
  +----> PostgreSQL
  |
  +----> Outbox Event
              |
              v
       Scheduled Publisher
              |
              v
            Kafka
              |
              v
           Consumer
```

---

# Main Ports

| Component | Port |
|---|---:|
| API Gateway | `8080` |
| Eureka Server | `8761` |
| StayRide Monolith | `9999` |
| Ride Service | `8081` |
| Monolith PostgreSQL (host) | `5433` |
| Ride PostgreSQL (host) | `5434` |
| Redis | `6379` |
| Kafka | `9092` |

The recommended client entry point is:

```text
http://localhost:8080
```

Eureka dashboard:

```text
http://localhost:8761
```

---

# Example API Endpoints

## Authentication

```http
POST /api/auth/login
```

## Users

```http
POST /api/users
GET /api/users/{id}
POST /api/users/hotel-admins
```

## Hotels

```http
POST /api/hotels
GET /api/hotels
GET /api/hotels/{id}
GET /api/hotels/{id}/rooms
```

## Bookings

```http
POST /api/bookings
DELETE /api/bookings/{bookingId}
```

## Drivers

```http
POST /api/drivers
```

## Rides

```http
POST /api/rides
GET /api/rides/{id}
```

Protected endpoints use:

```http
Authorization: Bearer <JWT>
```

Ride creation also expects:

```http
Idempotency-Key: <unique-key>
```

---

# Running the Project

## Prerequisites

For running services directly:

- Java 21
- Maven or the included Maven Wrapper
- PostgreSQL
- Redis
- Kafka

For containerized infrastructure:

- Docker
- Docker Compose

## Clone

```bash
git clone https://github.com/MdWafaJameel/stayride.git
cd stayride
```

## Run Individual Services

Each service is an independent Maven project.

### Service Discovery

```powershell
cd service-discovery
.\mvnw.cmd spring-boot:run
```

Runs on:

```text
http://localhost:8761
```

### StayRide Monolith

```powershell
cd stayride-monolith
.\mvnw.cmd spring-boot:run
```

Runs on:

```text
http://localhost:9999
```

### Ride Service

```powershell
cd ride-service
.\mvnw.cmd spring-boot:run
```

Runs on:

```text
http://localhost:8081
```

### API Gateway

```powershell
cd api-gateway
.\mvnw.cmd spring-boot:run
```

Runs on:

```text
http://localhost:8080
```

Start PostgreSQL, Redis, and Kafka before starting the services that depend on them.

The repository currently contains service-level Docker Compose configurations; a unified root-level Compose environment is a future improvement.

---

# Current Status

## Completed

- [x] User management
- [x] JWT authentication
- [x] Role-based authorization
- [x] Hotel management
- [x] Room management
- [x] Hotel-admin ownership authorization
- [x] Customer booking
- [x] Booking cancellation
- [x] DTO-based APIs
- [x] Global exception handling
- [x] Redis caching
- [x] Apache Kafka integration
- [x] Transactional Outbox Pattern
- [x] Scheduled outbox publishing
- [x] Dockerized application components
- [x] Ride Service
- [x] Separate Ride database
- [x] API Gateway
- [x] Eureka Service Discovery
- [x] Inter-service communication
- [x] Spring Cloud LoadBalancer
- [x] Thread pools
- [x] CompletableFuture
- [x] Parallel processing
- [x] Race-condition demonstration
- [x] Database pessimistic locking
- [x] Ride idempotency
- [x] Timeout
- [x] Retry
- [x] Exponential retry backoff
- [x] Circuit Breaker
- [x] Fallback
- [x] Bulkhead
- [x] Gateway rate limiting

## Next Improvements

- [ ] Unit testing with JUnit
- [ ] Mockito-based service tests
- [ ] Controller tests
- [ ] Integration testing
- [ ] Testcontainers
- [ ] Actuator and application metrics
- [ ] Structured logging
- [ ] Correlation IDs
- [ ] Distributed tracing
- [ ] API documentation
- [ ] Unified multi-service Docker Compose environment
- [ ] Further resilience and performance improvements

---

# Learning Objectives

StayRide is a practical backend project focused on understanding why common backend and distributed-system technologies are used.

The project demonstrates:

- Clean modular backend development
- Spring Boot application design
- REST API development
- Authentication and authorization
- Database design
- Caching
- Event-driven architecture
- Transactional messaging
- Microservices architecture
- Service discovery
- API Gateway patterns
- Inter-service communication
- Distributed-system challenges
- Concurrent programming
- Database-level concurrency control
- Fault tolerance and resilience
- Idempotent APIs
- Containerization

The goal is not simply to add technologies, but to understand the engineering problems they solve.

---

# Security

- Passwords are encoded before storage.
- JWTs provide stateless authentication.
- Spring Security enforces authorization.
- Role-based access control protects restricted operations.
- Resource ownership is checked in the service layer.
- Database credentials support environment-variable configuration.
- Secrets and credentials should never be committed to Git.

---

# Author

**Md Wafa Jameel**

GitHub: https://github.com/MdWafaJameel/stayride

---

## License

This project is currently intended as a personal portfolio and learning project.

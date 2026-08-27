# StayRide

StayRide is a Spring Boot backend for a hotel and room booking platform.

## Architecture

StayRide is currently designed as a **modular monolith**.

``` text
                         StayRide
                    Modular Monolith
                           |
          +----------------+----------------+
          |                |                |
        User             Hotel           Booking
        Module           Module           Module
          |                |                |
          +----------------+----------------+
                           |
                   Spring Security
                           |
                      JWT Authentication

                 Future separate service
                           |
                    Ride Microservice
                           |
                    Driver / Ride
```

## Current Features

-   User registration
-   JWT-based authentication
-   Spring Security authorization
-   Role-based access control
-   Customer booking and cancellation
-   Hotel administrator management
-   Hotel and room management
-   Hotel-admin ownership authorization
-   Booking ownership authorization
-   Global exception handling
-   DTO-based API requests and responses

## Roles

  Role            Responsibilities
  --------------- -------------------------------------------------------
  `CUSTOMER`      Book rooms and cancel their own bookings
  `HOTEL_ADMIN`   Create and manage hotels and rooms they own
  `ADMIN`         Perform both customer-side and hotel-admin operations

`DRIVER` is intentionally not part of the main application's role enum.
Driver and ride functionality is planned as a separate microservice.

## Authentication

StayRide uses stateless JWT authentication with Spring Security.

``` text
email + password
      |
      v
AuthenticationManager
      |
      v
CustomUserDetailsService
      |
      v
User database
      |
      v
JwtService
      |
      v
JWT
```

For protected requests:

``` text
Bearer JWT
    |
    v
JwtAuthenticationFilter
    |
    v
SecurityContext
    |
    +-- user ID
    +-- role
    |
    v
Controller
    |
    v
Service
```

The authenticated user's identity comes from the JWT. Business ownership
is then verified against the application's database.

## Hotel Administration

``` text
ADMIN
  |
  | creates
  v
HOTEL_ADMIN User
  |
  | logs in
  v
JWT
  |
  | creates
  v
Hotel
  |
  | creates
  v
Room
```

Hotel-admin operations verify that the authenticated administrator owns
the hotel being modified.

## Booking

``` text
CUSTOMER
   |
   | login
   v
JWT
   |
   | book room
   v
Booking
   |
   +--> User
   |
   +--> Room
          |
          +--> Hotel
```

A booking cancellation verifies that the authenticated customer owns the
booking.

## Technology Stack

-   Java
-   Spring Boot
-   Spring Security
-   JWT
-   Spring Data JPA
-   Maven
-   Docker
-   Relational database
-   Lombok
-   Jakarta Validation

### Planned

-   Redis for caching
-   Apache Kafka for event-driven communication
-   Separate Ride microservice
-   Driver and ride management

These are planned features and are not claimed as completed
functionality.

## Project Structure

``` text
src/main/java/com/stayride/
├── booking/
├── common/
│   ├── config/
│   ├── exception/
│   └── security/
├── hotel/
├── ride/
└── user/
```

The project is organized around business modules rather than one large
global controller/service/entity structure.

## API Overview

### Authentication

``` http
POST /api/auth/login
```

### User Registration

``` http
POST /api/users
```

Public registration creates a customer account.

### Create Hotel Admin

``` http
POST /api/users/hotel-admins
```

This operation is restricted to `ADMIN`.

### Book a Room

``` http
POST /api/bookings
```

### Cancel a Booking

``` http
DELETE /api/bookings/{bookingId}
```

Protected endpoints use:

``` http
Authorization: Bearer <JWT>
```

User IDs are not trusted from the client for booking ownership. The
authenticated identity comes from the JWT.

## Running Locally

### Prerequisites

-   Java
-   Maven
-   Docker
-   A configured relational database

Clone the repository:

``` bash
git clone https://github.com/MdWafaJameel/stayride.git
cd stayride
```

Run with the Maven wrapper on Windows:

``` bash
mvnw.cmd spring-boot:run
```

Or with Maven installed globally:

``` bash
mvn spring-boot:run
```

Do not commit database passwords, JWT secrets, API keys, or other
credentials.

## Git Workflow

Create a feature branch:

``` bash
git checkout -b feature/redis
```

Commit changes:

``` bash
git add .
git commit -m "feat: add redis caching"
```

Push the branch:

``` bash
git push -u origin feature/redis
```

The `main` branch is kept as the stable project branch.

## Roadmap

### Completed

-   [x] User management
-   [x] JWT authentication
-   [x] Role-based authorization
-   [x] Customer booking
-   [x] Booking cancellation
-   [x] Hotel administration
-   [x] Room management
-   [x] Ownership-based authorization

### Planned

-   [ ] Improve automated test coverage
-   [ ] API documentation
-   [ ] Redis caching
-   [ ] Kafka event-driven booking workflow
-   [ ] Extract Ride functionality into a separate microservice
-   [ ] Driver and ride management
-   [ ] Production configuration and observability

## Security

-   Passwords are encoded before storage.
-   JWTs provide stateless authentication.
-   Spring Security roles enforce authorization.
-   Resource ownership is checked in the service layer.
-   Secrets should be supplied through environment-specific
    configuration and never committed to Git.

## Author

**Md Wafa Jameel**

GitHub: https://github.com/MdWafaJameel/stayride

## License

This project is currently intended as a personal portfolio and learning
project.

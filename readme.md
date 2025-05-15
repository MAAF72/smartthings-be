## Technology Stack

### Core Components
- **Language**: Java 21
- **Build Tool**: Gradle 8.14
- **Web Framework**: Ratpack 2.0.0
- **Architecture**: Domain-Driven Design (DDD)

### Infrastructure
- **Database**: PostgreSQL
- **ORM**: Hibernate 6.6.14
- **Migrations**: Flyway 11
- **Dependency Injection**: Weld 6

### Security
- **Authentication**: JWT (JSON Web Tokens)


## Getting Started

### Prerequisites
- Java 21 JDK
- Gradle 8.14+
- PostgreSQL 16+

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/MAAF72/smartthings-be.git
   cd smartthings-be
2. Configure environment variables (create .env file):
   ```bash
   cp .env.example .env
   ```
   Fill the .env file with your database credentials.

3. Migrate the database:
   ```bash
   gradle flywayMigrate
   ```
4. Run the application:
   ```bash
   gradle run
   ```
5. Access the application at http://localhost:8080

## Project Structure
```
src 
└── main
    ├── java
    │   └── io
    │       └── github
    │           └── maaf72
    │               └── smartthings             # Base package for the application
    │                   ├── Main.java           # Application entry point
    │                   ├── config              # Global configurations
    │                   ├── domain              # Core Business Logic
    │                   │   ├── device          
    │                   │   │   ├── dto         # Data Transfer Object for request/response models
    │                   │   │   ├── entity      # Domain models
    │                   │   │   ├── handler     # Delivery layer, HTTP route handlers
    │                   │   │   ├── repository  # Data layer, for database operations
    │                   │   │   └── usecase     # Business logic layer
    │                   ├── infra
    │                   │   ├── database        # Database utilities
    │                   │   ├── exception       # Global error handling
    │                   │   ├── mapper          # Object mapper
    │                   │   ├── middleware      # HTTP middleware
    │                   │   └── security        # Security utilities
    │                   └── itf                 # Abstractions to decouple layers
    └── resources
        ├── META-INF
        └── database
            └── migration                       # Database schema scripts
statics                                         # Static files served by the application
```

## Configuration

### Database Configuration

Configure these environment variables:
- `APP_DATABASE_DRIVER`: JDBC driver class names 
- `APP_DATABASE_JDBC_URL`: JDBC URL for the database
- `APP_DATABASE_USERNAME`: Database username
- `APP_DATABASE_PASSWORD`: Database password

### Database Setup

Flyway migrations executed via `gradle flywayMigrate`. \
Create new migrations in: `src/main/resources/database/migration/V*__description.sql`

### JWT Configuration

Configure these environment variables:
- `APP_JWT_SECRET_KEY`: Secret key for token signing
- `APP_JWT_TOKEN_DURATION`: Token expiration time (default: 86400000 milliseconds / 24 hours)

## API Documentation
The application exposes REST endpoints documented via OpenAPI. Access Swagger UI at: ```http://localhost:8080/swagger```
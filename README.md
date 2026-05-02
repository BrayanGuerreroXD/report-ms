# Report Microservice

Spring WebFlux API for generating reports from bootcamp data, consuming events via Kafka and exposing reactive REST endpoints.

## Technologies
- Spring WebFlux
- Spring Security
- Spring Data MongoDB Reactive
- Apache Kafka
- Java 25
- Gradle
- MongoDB
- MapStruct
- OpenApi Swagger

## Architecture

This project follows **Clean Architecture** principles as implemented in the Bancolombia scaffold. The architecture is organized into independent layers that facilitate maintenance and scalability.

### Project Structure (Based on Bancolombia Scaffold)

```
report-ms/
├── applications/                 # Application layer (entry points)
│   └── app-service/             # Main application service
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/        # Main source code
│       │   │   └── resources/   # Configuration resources
│       │   └── test/            # Tests
│       └── build.gradle         # Gradle configuration for the service
├── domain/                      # Domain layer (pure business)
│   ├── model/                   # Entities and domain models
│   └── usecase/                 # Application use cases
├── infrastructure/              # Infrastructure layer (technical details)
│   ├── driven-adapters/         # Adapters to external systems
│   │   └── mongodb-repository/ # MongoDB repository adapter
│   └── entry-points/            # Application entry points
│       ├── reactive-web/        # Reactive web adapter (WebFlux)
│       └── kafkaconsumer/      # Kafka consumer adapter
├── deployment/                  # Deployment configurations
├── build.gradle                 # Root Gradle configuration
├── settings.gradle              # Multi-project configuration
└── README.md                    # This file
```

### Layer Details

1. **Domain**: Contains pure business logic, independent of frameworks and technologies.
   - `model`: Entities representing business concepts (Bootcamp, Person, Capacity, Technology, Auth)
   - `usecase`: Implementation of use cases that orchestrate application logic

2. **Infrastructure**: Technical implementation details.
   - `driven-adapters`: Adapters that allow the domain to communicate with the outside world (MongoDB repositories)
   - `entry-points`: System entry points (APIs, message queues, etc.)

3. **Applications**: Specific configuration for each service/application.
   - Contains the main class with the `main` method
   - Configures beans and dependencies specific to the service

## Configuration

Example configuration in `applications/app-service/src/main/resources/application.yaml`:

```yaml
server:
  port: 8080
spring:
  application:
    name: report-ms
  data:
    mongodb:
      uri: mongodb://admin:secret123@localhost:27017/webflux_db?authSource=admin
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: report-ms-group
      auto-offset-reset: earliest

kafka:
  topics:
    bootcamp-report-sync: bootcamp.report.sync
```

## Main Endpoints

### Bootcamps
- `GET /api/v1/bootcamps/max-technology-count` - Get the bootcamp with the maximum number of technologies

### Security
- All endpoints except `/actuator/**` and Swagger documentation require authentication
- Unauthorized requests return HTTP 401
- Access denied returns HTTP 403

## Kafka Events

### Consumed Events

**Bootcamp Sync Event** (`bootcamp.report.sync` topic):
- Receives bootcamp data including name, description, duration, capacities, and people
- Triggers synchronization of bootcamp information into the local MongoDB database
- Automatically calculates and stores counts for persons, capacities, and technologies

## Development Commands

### Start the API

```bash
# From the project root
./gradlew applications:app-service:bootRun
```

The API will be available at `http://localhost:8080`

### Run Tests

```bash
# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew applications:app-service:test
./gradlew domain:model:test
./gradlew domain:usecase:test
./gradlew infrastructure:driven-adapters:mongodb-repository:test
```

### Generate OpenApi Documentation (Swagger)

Once the application is running, access:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenApi JSON: `http://localhost:8080/v3/api-docs`

## Implemented Features

- Clean Architecture following Bancolombia principles
- Reactive programming with Spring WebFlux and Spring Data MongoDB Reactive
- Authentication and authorization with Spring Security
- Kafka consumer for event-driven bootcamp synchronization
- MapStruct for entity/DTO mapping
- Global exception handling
- CORS configuration
- Health checks and metrics with Actuator
- Database migrations with Flyway (if applicable)

## Prerequisites

- Java 25
- Gradle 8.x
- MongoDB 6.x
- Apache Kafka
- Docker (optional, for development with containers)

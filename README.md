# Todo Service API

A RESTful API service for managing Todo items built with Spring Boot 3.5, Java 21, and PostgreSQL.

## Features

- ✅ Full CRUD operations for Todo items
- ✅ Status management (OPEN, IN_PROGRESS, DONE)
- ✅ Due date tracking
- ✅ Pagination and filtering support
- ✅ Bean Validation for request validation
- ✅ RFC 7807 Problem Details for error handling
- ✅ PostgreSQL database with JPA/Hibernate
- ✅ Docker Compose support for local development
- ✅ TestContainers for integration testing
- ✅ Comprehensive test coverage (32 tests)

## Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **Docker** (for Docker Compose and TestContainers)

## Quick Start

### 1. Clone the repository

```bash
git clone https://github.com/wahyuuk/todo-service.git
cd todo-service
```

### 2. Run with Docker Compose (Recommended for Development)

Spring Boot will automatically start PostgreSQL using Docker Compose:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

This will:
- Start PostgreSQL container automatically
- Create the database
- Start the application on http://localhost:8080
- Stop PostgreSQL when you terminate the app

### 3. Run Tests

```bash
./mvnw test
```

Tests use TestContainers to automatically spin up PostgreSQL containers.

## API Endpoints

### Create Todo
```http
POST /api/todos
Content-Type: application/json

{
  "title": "Learn Spring Boot",
  "description": "Study Spring Boot documentation",
  "dueDate": "2025-12-31T23:59:59Z"
}
```

### Get All Todos
```http
GET /api/todos?status=OPEN&page=0&size=10
```

### Get Todo by ID
```http
GET /api/todos/{id}
```

### Update Todo Status
```http
PATCH /api/todos/{id}/status
Content-Type: application/json

{
  "status": "DONE"
}
```

### Delete Todo
```http
DELETE /api/todos/{id}
```

## Project Structure

```
todo-service/
├── src/
│   ├── main/
│   │   ├── java/com/kuncoro/todo/
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── service/         # Business logic
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── domain/          # Entity models
│   │   │   ├── dto/             # Data transfer objects
│   │   │   └── exception/       # Exception handling
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-dev.properties
│   └── test/
│       ├── java/com/kuncoro/todo/
│       │   ├── controller/      # Controller integration tests
│       │   ├── service/         # Service tests (unit & integration)
│       │   ├── repository/      # Repository integration tests
│       │   └── config/          # Test configuration (TestContainers)
│       └── resources/
│           └── application-test.properties
├── compose.yaml                  # Docker Compose configuration
├── pom.xml                      # Maven configuration
└── docs/
    ├── specs.md                 # API specifications
    ├── ci-workflow.md           # CI/CD documentation
    └── docker-compose-integration.md  # Docker Compose guide
```

## Development Workflows

### Option 1: Spring Boot Managed Docker Compose (Recommended)

```bash
# Start application (PostgreSQL starts automatically)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Make code changes - DevTools will auto-reload

# Stop with Ctrl+C (PostgreSQL stops automatically)
```

### Option 2: Manual Docker Compose

```bash
# Terminal 1: Start PostgreSQL
docker compose up postgres

# Terminal 2: Run application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Cleanup
docker compose down
```

### Running Tests

```bash
# Run all tests
./mvnw test

# Run only integration tests
./mvnw test -Dtest="*IntegrationTest"

# Run specific test class
./mvnw test -Dtest=TodoControllerIntegrationTest

# Run with coverage
./mvnw clean test jacoco:report
```

## Configuration Profiles

### Production Profile (default)
- Uses external PostgreSQL database
- Configuration in `application.properties`

### Development Profile (`dev`)
- Uses Docker Compose managed PostgreSQL
- SQL logging enabled
- Configuration in `application-dev.properties`

### Test Profile (`test`)
- Uses TestContainers for isolated testing
- Fresh database for each test suite
- Configuration in `application-test.properties`

## Database Configuration

### Docker Compose (Development)
```yaml
Database: todo_db
Username: todo_user
Password: todo_password
Port: 5432
```

### TestContainers (Testing)
```yaml
Database: todo_test
Username: test
Password: test
Port: Random (managed by TestContainers)
```

## Technology Stack

- **Spring Boot 3.5.6** - Application framework
- **Java 21** - Programming language
- **PostgreSQL 15** - Database
- **Spring Data JPA** - Data access
- **Hibernate** - ORM
- **Bean Validation** - Input validation
- **Lombok** - Boilerplate code reduction
- **Docker Compose** - Local development
- **TestContainers** - Integration testing
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework
- **AssertJ** - Fluent assertions

## Testing

The project has comprehensive test coverage with 32 tests:

- **Unit Tests** (9 tests): Test business logic in isolation with mocks
- **Integration Tests** (23 tests):
  - Repository layer: 6 tests
  - Service layer: 5 tests  
  - Controller layer: 12 tests

All integration tests use TestContainers with PostgreSQL 15-alpine.

## Docker Support

### Docker Compose Features
- Automatic lifecycle management
- Persistent data via volumes
- Easy to add more services
- Consistent development environment

### TestContainers Features
- Isolated test environments
- No port conflicts
- Automatic cleanup
- CI/CD friendly

See [docs/docker-compose-integration.md](docs/docker-compose-integration.md) for detailed documentation.

## Building

```bash
# Clean and build
./mvnw clean package

# Build without tests
./mvnw clean package -DskipTests

# Build and run
./mvnw clean package && java -jar target/todo-0.0.1-SNAPSHOT.jar
```

## Troubleshooting

### Port 5432 already in use

Stop local PostgreSQL or change the port in `compose.yaml`:

```yaml
ports:
  - '5433:5432'
```

Then update `application-dev.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/todo_db
```

### Docker not found

Ensure Docker is installed and running:

```bash
docker --version
docker compose version
```

### Tests failing

Ensure Docker is running (required for TestContainers):

```bash
docker info
```

## CI/CD

The project uses GitHub Actions for continuous integration. See [docs/ci-workflow.md](docs/ci-workflow.md) for details.

## Documentation

- [API Specifications](docs/specs.md)
- [Docker Compose Integration](docs/docker-compose-integration.md)
- [CI/CD Workflow](docs/ci-workflow.md)
- [PostgreSQL Migration Notes](POSTGRESQL_MIGRATION.md)

## License

This project is for educational purposes.

## Author

Wahyu Kuncoro

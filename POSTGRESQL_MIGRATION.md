# PostgreSQL Migration and TestContainers Integration

## Changes Made

This project has been successfully migrated from H2 to PostgreSQL with comprehensive integration testing using TestContainers and Docker Compose support for local development.

### Database Migration
- **From**: H2 in-memory database
- **To**: PostgreSQL (production) + Docker Compose (development) + TestContainers (testing)

### Dependencies Added
- `org.postgresql:postgresql` - PostgreSQL JDBC driver
- `org.testcontainers:junit-jupiter` - TestContainers JUnit integration
- `org.testcontainers:postgresql` - PostgreSQL TestContainer
- `org.springframework.boot:spring-boot-testcontainers` - Spring Boot TestContainers support
- `org.springframework.boot:spring-boot-docker-compose` - Spring Boot Docker Compose support

### Configuration Changes
- `application.properties`: PostgreSQL configuration for production
- `application-dev.properties`: Development profile with Docker Compose support
- `application-test.properties`: Test-specific configuration
- `compose.yaml`: Docker Compose configuration for local development
- TestContainers configuration class for integration tests

### Integration Tests Created
1. **TodoRepositoryIntegrationTest** - 6 tests covering full CRUD operations
2. **TodoServiceIntegrationTest** - 5 tests covering business logic integration
3. **TodoControllerIntegrationTest** - 12 tests covering HTTP endpoints end-to-end

### Test Results
- **Total Tests**: 32 
- **Passing**: 32 (100%)
- **Unit Tests**: 9 tests (original tests still passing)
- **Integration Tests**: 23 tests (new PostgreSQL integration tests)

### Technical Notes
- All integration tests use PostgreSQL 15-alpine via TestContainers
- Local development can use Docker Compose with Spring Boot auto-management
- Database schema is auto-created using Hibernate DDL
- Tests are isolated - each test gets a fresh database container
- Automatic timestamp generation (`@CreationTimestamp`/`@UpdateTimestamp`) works in production but is disabled in test context
- All core functionality (CRUD, validation, error handling) fully tested with PostgreSQL

### Running the Tests
```bash
# Run all tests (uses TestContainers automatically)
./mvnw test

# Run only integration tests
./mvnw test -Dtest="*IntegrationTest"

# Run specific test class
./mvnw test -Dtest="TodoRepositoryIntegrationTest"
```

### Running in Development Mode
```bash
# Run with Docker Compose (auto-starts PostgreSQL)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Or manually manage Docker Compose
docker compose up -d
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
docker compose down
```

### Requirements
- Docker (for both Docker Compose and TestContainers)
- Java 21
- Maven 3.6+

For detailed Docker Compose integration documentation, see [docs/docker-compose-integration.md](docs/docker-compose-integration.md).

The migration is complete and all tests pass successfully with the new PostgreSQL setup.
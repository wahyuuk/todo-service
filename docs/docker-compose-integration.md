# Spring Docker Compose Integration

## Overview

This project now supports **Spring Boot Docker Compose** integration, which provides automatic lifecycle management of Docker containers during development and testing. This feature complements the existing TestContainers setup for integration tests.

## Features

### 1. Automatic Service Startup
When you run the application in development mode, Spring Boot will automatically:
- Detect the `compose.yaml` file in the project root
- Start PostgreSQL container using Docker Compose
- Configure the application's datasource to connect to the container
- Stop the container when the application shuts down

### 2. Dual Approach
The project now supports two complementary approaches for database integration:

- **Docker Compose (Development)**: For local development with `application-dev.properties`
- **TestContainers (Testing)**: For automated integration tests with `application-test.properties`

## Configuration Files

### compose.yaml
Located in the project root, defines the PostgreSQL service:

```yaml
services:
  postgres:
    image: 'postgres:15-alpine'
    environment:
      - 'POSTGRES_DB=todo_db'
      - 'POSTGRES_USER=todo_user'
      - 'POSTGRES_PASSWORD=todo_password'
    ports:
      - '5432:5432'
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### application-dev.properties
Configuration for development profile with Docker Compose support:

```properties
spring.application.name=todo-dev

# Docker Compose support
spring.docker.compose.enabled=true
spring.docker.compose.lifecycle-management=start_and_stop

# Database configuration (matches compose.yaml)
spring.datasource.url=jdbc:postgresql://localhost:5432/todo_db
spring.datasource.username=todo_user
spring.datasource.password=todo_password
```

## Usage

### Running in Development Mode

1. **Start the application with dev profile:**
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```
   
   Docker Compose will automatically:
   - Start PostgreSQL container
   - Wait for it to be ready
   - Configure the datasource
   - Keep it running while the app runs
   - Stop it when you terminate the app

2. **Manual Docker Compose control:**
   ```bash
   # Start services manually
   docker compose up -d
   
   # Stop services manually
   docker compose down
   
   # View logs
   docker compose logs -f postgres
   ```

### Running Tests

Tests continue to use **TestContainers** automatically:

```bash
# Run all tests (uses TestContainers)
./mvnw test

# Run specific test class
./mvnw test -Dtest=TodoRepositoryIntegrationTest
```

TestContainers will:
- Spin up isolated PostgreSQL containers for each test suite
- Ensure test isolation
- Clean up containers after tests complete

## Development Workflow

### Option 1: Spring Boot Managed (Recommended)
Let Spring Boot manage the lifecycle:

```bash
# Terminal 1: Run application (starts PostgreSQL automatically)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Terminal 2: Make code changes, Spring DevTools will auto-reload

# Stop with Ctrl+C (stops PostgreSQL automatically)
```

### Option 2: Manual Docker Compose
Manage Docker Compose manually if you need more control:

```bash
# Terminal 1: Start PostgreSQL
docker compose up postgres

# Terminal 2: Run application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Cleanup
docker compose down
```

## Benefits

### Docker Compose Benefits
- ✅ Realistic local development environment
- ✅ Persistent data across application restarts (via volumes)
- ✅ Easy to add additional services (Redis, Kafka, etc.)
- ✅ Consistent environment across team members
- ✅ No need to install PostgreSQL locally

### TestContainers Benefits
- ✅ Isolated test environment
- ✅ Fresh database for each test suite
- ✅ No port conflicts
- ✅ Automatically cleaned up
- ✅ Works in CI/CD pipelines

## Docker Compose Configuration Options

The `application-dev.properties` file provides several configuration options:

```properties
# Enable/disable Docker Compose support
spring.docker.compose.enabled=true

# Lifecycle management strategy
# Options: start_and_stop, start_only, none
spring.docker.compose.lifecycle-management=start_and_stop

# Start command (default: up)
spring.docker.compose.start.command=up

# Stop command (default: down)
spring.docker.compose.stop.command=down

# Timeout for stop operation
spring.docker.compose.stop.timeout=1m

# Docker Compose file location (default: compose.yaml)
spring.docker.compose.file=compose.yaml
```

## Troubleshooting

### Docker Compose not starting
1. **Check Docker is running:**
   ```bash
   docker info
   ```

2. **Check Docker Compose is installed:**
   ```bash
   docker compose version
   ```

3. **View Spring Boot Docker Compose logs:**
   Enable debug logging in `application-dev.properties`:
   ```properties
   logging.level.org.springframework.boot.docker.compose=DEBUG
   ```

### Port 5432 already in use
If you have PostgreSQL running locally:

1. **Stop local PostgreSQL:**
   ```bash
   sudo systemctl stop postgresql  # Linux
   brew services stop postgresql    # macOS
   ```

2. **Or change the port in compose.yaml:**
   ```yaml
   ports:
     - '5433:5432'  # Use different host port
   ```
   
   And update `application-dev.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5433/todo_db
   ```

### Tests failing with Docker Compose
Tests use TestContainers, not Docker Compose. If tests fail:

1. **Ensure Docker is running** (TestContainers needs Docker)
2. **Check test logs** for container startup issues
3. **Verify test profile** is active (`application-test.properties`)

## Requirements

- **Docker**: Version 20.10+ 
- **Docker Compose**: Version 2.0+ (comes with Docker Desktop)
- **Java**: 21+
- **Maven**: 3.6+

## Comparison: Docker Compose vs TestContainers

| Feature | Docker Compose | TestContainers |
|---------|---------------|----------------|
| **Purpose** | Local development | Integration testing |
| **Lifecycle** | Application managed | Test managed |
| **Data Persistence** | Yes (volumes) | No (ephemeral) |
| **Startup Time** | One-time per session | Per test suite |
| **Port** | Fixed (5432) | Random |
| **Isolation** | Shared across runs | Isolated per test |
| **CI/CD** | Manual setup | Automatic |

## Adding More Services

To add additional services (Redis, Kafka, etc.), simply update `compose.yaml`:

```yaml
services:
  postgres:
    # ... existing config ...
  
  redis:
    image: 'redis:7-alpine'
    ports:
      - '6379:6379'
  
  kafka:
    image: 'confluentinc/cp-kafka:latest'
    ports:
      - '9092:9092'
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
```

Spring Boot will automatically detect and manage these services!

## References

- [Spring Boot Docker Compose Support](https://docs.spring.io/spring-boot/reference/features/docker-compose.html)
- [TestContainers Documentation](https://www.testcontainers.org/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)

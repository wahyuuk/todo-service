# Quick Reference Guide

## Running the Application

### Local Development (with Docker Compose)
```bash
# Option 1: Let Spring Boot manage Docker Compose (Recommended)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Option 2: Manual Docker Compose
docker compose up -d
./mvnw spring-boot:run
docker compose down
```

### Production Mode
```bash
# Requires external PostgreSQL server
./mvnw spring-boot:run

# Or with packaged JAR
./mvnw clean package
java -jar target/todo-0.0.1-SNAPSHOT.jar
```

## Testing

### Run All Tests
```bash
./mvnw test
```

### Run Specific Test Types
```bash
# Unit tests only
./mvnw test -Dtest=TodoServiceTest

# Integration tests only
./mvnw test -Dtest="*IntegrationTest"

# Specific test class
./mvnw test -Dtest=TodoControllerIntegrationTest

# Specific test method
./mvnw test -Dtest=TodoServiceTest#create_ShouldReturnSavedTodoResponse
```

## Docker Commands

### Docker Compose
```bash
# Start all services
docker compose up -d

# Start specific service
docker compose up -d postgres

# Stop all services
docker compose down

# Stop and remove volumes (deletes data)
docker compose down -v

# View logs
docker compose logs -f postgres

# Check status
docker compose ps
```

### Docker Container Management
```bash
# List running containers
docker ps

# View PostgreSQL logs
docker logs todo-service-postgres-1

# Connect to PostgreSQL
docker exec -it todo-service-postgres-1 psql -U todo_user -d todo_db

# Stop all containers
docker stop $(docker ps -q)
```

## Database Access

### Via Docker Compose
```bash
# Connect to running PostgreSQL container
docker exec -it todo-service-postgres-1 psql -U todo_user -d todo_db

# SQL commands
\dt           # List tables
\d todos      # Describe todos table
SELECT * FROM todos;
\q            # Quit
```

### Connection Details (Development)
- **Host**: localhost
- **Port**: 5432
- **Database**: todo_db
- **Username**: todo_user
- **Password**: todo_password

## Maven Commands

### Building
```bash
# Clean and compile
./mvnw clean compile

# Package (create JAR)
./mvnw clean package

# Skip tests
./mvnw clean package -DskipTests

# Install to local repository
./mvnw clean install
```

### Running
```bash
# Run application
./mvnw spring-boot:run

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run with debug mode
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Testing
```bash
# Run tests
./mvnw test

# Run tests with verbose output
./mvnw test -X

# Run tests and generate coverage report
./mvnw clean test jacoco:report
```

## API Testing with cURL

### Create Todo
```bash
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn Spring Boot",
    "description": "Study Spring Boot documentation",
    "dueDate": "2025-12-31T23:59:59Z"
  }'
```

### Get All Todos
```bash
curl http://localhost:8080/api/todos

# With pagination and filtering
curl "http://localhost:8080/api/todos?status=OPEN&page=0&size=10"
```

### Get Todo by ID
```bash
curl http://localhost:8080/api/todos/{todo-id}
```

### Update Todo Status
```bash
curl -X PATCH http://localhost:8080/api/todos/{todo-id}/status \
  -H "Content-Type: application/json" \
  -d '{"status": "DONE"}'
```

### Delete Todo
```bash
curl -X DELETE http://localhost:8080/api/todos/{todo-id}
```

## Troubleshooting

### Port 5432 Already in Use
```bash
# Check what's using the port
sudo lsof -i :5432

# Stop local PostgreSQL (if installed)
sudo systemctl stop postgresql    # Linux
brew services stop postgresql      # macOS

# Or use different port in compose.yaml
```

### Application Won't Start
```bash
# Check Java version (must be 21+)
java -version

# Check Docker is running
docker info

# Check Docker Compose version
docker compose version

# View application logs with debug
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev -Dlogging.level.root=DEBUG
```

### Tests Failing
```bash
# Ensure Docker is running (for TestContainers)
docker info

# Clean and retry
./mvnw clean test

# Run with verbose output
./mvnw test -X

# Check specific test logs
./mvnw test -Dtest=TodoRepositoryIntegrationTest -X
```

### Docker Compose Issues
```bash
# Validate compose.yaml
docker compose config

# Force recreate containers
docker compose up -d --force-recreate

# Remove all data and start fresh
docker compose down -v
docker compose up -d
```

## Development Tips

### Hot Reload
Spring DevTools is included. Code changes will auto-reload when running with:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Database Persistence
Data persists in Docker volumes. To reset:
```bash
docker compose down -v
docker compose up -d
```

### Debugging
```bash
# Start with debug port 5005
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"

# Attach your IDE debugger to localhost:5005
```

### Code Style
The project uses standard Spring Boot conventions. Follow existing patterns in the codebase.

## Profiles Summary

| Profile | Database | Auto-Start | Data Persistence | Use Case |
|---------|----------|-----------|------------------|----------|
| default | External PostgreSQL | No | Yes | Production |
| dev | Docker Compose | Yes | Yes | Local development |
| test | TestContainers | Yes | No | Automated testing |

## Useful Links

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [TestContainers Documentation](https://www.testcontainers.org/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

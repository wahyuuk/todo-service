# CI/CD Workflow

## GitHub Actions CI Workflow

This project includes a GitHub Actions CI workflow that automatically runs Maven commands when:

1. **Pull Request** is created or updated against the `main` branch
2. **Push** is made to the `main` branch (e.g., when PR is merged)

### Workflow Features

The CI workflow (`.github/workflows/ci.yml`) includes the following steps:

1. **Environment Setup**
   - Checkout source code
   - Set up JDK 21 (Temurin distribution)
   - Cache Maven dependencies for faster builds

2. **Build Process**
   - Make Maven wrapper executable
   - Clean and validate the project
   - Compile the source code
   - Run unit tests
   - Package the application
   - Verify build artifacts

### Maven Commands Executed

The following Maven commands are executed in sequence:

```bash
./mvnw clean validate    # Clean and validate project structure
./mvnw compile          # Compile source code
./mvnw test            # Run unit tests
./mvnw package -DskipTests  # Create JAR package (tests already ran)
```

### Requirements

- Java 21 (automatically set up by the workflow)
- Maven (uses included Maven wrapper `./mvnw`)

### Triggers

The CI workflow runs on:
- Pull requests targeting `main` branch
- Direct pushes to `main` branch

This ensures code quality and prevents broken builds from being merged into the main branch.